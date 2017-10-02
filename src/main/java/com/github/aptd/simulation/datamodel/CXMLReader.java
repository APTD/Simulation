/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Asimov - Agentbased Passenger Train Delay                 #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package com.github.aptd.simulation.datamodel;

import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.experiment.local.CExperiment;
import com.github.aptd.simulation.core.messaging.local.CMessenger;
import com.github.aptd.simulation.core.statistic.IStatistic;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.core.time.local.CJumpTime;
import com.github.aptd.simulation.core.time.local.CStepTime;
import com.github.aptd.simulation.datamodel.xml.AgentRef;
import com.github.aptd.simulation.datamodel.xml.Asimov;
import com.github.aptd.simulation.datamodel.xml.Iagent;
import com.github.aptd.simulation.datamodel.xml.Iagents;
import com.github.aptd.simulation.datamodel.xml.Network;
import com.github.aptd.simulation.datamodel.xml.PlatformType;
import com.github.aptd.simulation.datamodel.xml.StationLayout;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.graph.network.IPlatform;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.github.aptd.simulation.elements.passenger.IPassengerSource;
import com.github.aptd.simulation.elements.train.CTrain;
import com.github.aptd.simulation.elements.train.IDoor;
import com.github.aptd.simulation.elements.train.ITrain;
import com.github.aptd.simulation.error.CNotFoundException;
import com.github.aptd.simulation.error.CRuntimeException;
import com.github.aptd.simulation.error.CSemanticException;
import com.github.aptd.simulation.factory.IFactory;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.common.CCommon;
import org.railml.schemas._2016.EArrivalDepartureTimes;
import org.railml.schemas._2016.EFormation;
import org.railml.schemas._2016.EOcp;
import org.railml.schemas._2016.EOcpTT;
import org.railml.schemas._2016.ETrack;
import org.railml.schemas._2016.ETrainPart;
import org.railml.schemas._2016.EVehicle;
import org.railml.schemas._2016.TDoors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * data-model XML reader
 *
 * @bug incomplete
 */
public final class CXMLReader implements IDataModel
{
    private static final QName PLATFORM_REF_ATTRIBUTE = new QName(
        "https://raw.githubusercontent.com/APTD/Simulation/master/src/main/xsd",
        "platformRef"
    );
    /**
     * jaxb context
     */
    private final JAXBContext m_context;

    /**
     * ctor
     */
    public CXMLReader()
    {
        try
        {
            m_context = JAXBContext.newInstance( Asimov.class, AgentRef.class, StationLayout.class );
        }
        catch ( final JAXBException l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IExperiment get( final IFactory p_factory, final String p_datamodel,
                                  final long p_simulationsteps, final boolean p_parallel, final String p_timemodel,
                                  final Supplier<RealDistribution> p_passengerspeedatstationdistributionsupplier
    )
    {
        try
        (
            final FileInputStream l_stream = new FileInputStream( p_datamodel )
        )
        {
            final Asimov l_model = (Asimov) m_context.createUnmarshaller().unmarshal( l_stream );

            // time definition

            final Instant l_starttime = ZonedDateTime.now( ZoneId.systemDefault() )
                                                     .with( ChronoField.CLOCK_HOUR_OF_DAY, 9 )
                                                     .with( ChronoField.MINUTE_OF_HOUR, 45 )
                                                     .toInstant();

            final ITime l_time = "jump".equals( p_timemodel ) ? new CJumpTime( l_starttime ) : new CStepTime( l_starttime, Duration.ofSeconds( 1 ) );

            final CMessenger l_messenger = new CMessenger();

            final Set<IAction> l_actionsfrompackage = CCommon.actionsFromPackage().collect( Collectors.toSet() );

            // asl agent definition
            final Map<String, String> l_agentdefs = agents( l_model.getAi() );

            // macro (train-network) and microscopic model
            final Map<String, IPlatform<?>> l_platform = platform( l_model.getNetwork(), l_agentdefs, p_factory, l_time );
            final Map<String, IStation<?>> l_station = station( l_model.getNetwork(), l_agentdefs, p_factory, l_time, l_platform );
            final Pair<Map<String, ITrain<?>>, Map<String, IDoor<?>>> l_train = train( l_model.getNetwork(), l_agentdefs, p_factory, l_time );

            final Map<String, IElement<?>> l_agents = new HashMap<>();
            l_agents.putAll( l_platform );
            l_agents.putAll( l_station );
            l_agents.putAll( l_train.getLeft() );
            l_agents.putAll( l_train.getRight() );

            final CExperiment l_experiment = new CExperiment( p_simulationsteps, p_parallel, IStatistic.EMPTY, l_agents,
                    l_time, l_messenger );

            // @todo create passengersources and their passenger generators according to scenario definition

            final IElement.IGenerator<IPassenger<?>> l_passengergenerator = passengergenerator( p_factory,
                                                                                                "+!activate <-\n    state/transition\n.",
                    l_actionsfrompackage, l_time );

            l_experiment.addAgent( "passengersource_test",
                    passengersourcegenerator(
                            p_factory, "+!activate <-\n    state/transition\n.",
                            l_actionsfrompackage, l_time )
                            .generatesingle( new UniformRealDistribution( 0.0, 1800000.0 ),
                                             l_time.current().toEpochMilli(), 20, l_passengergenerator, l_experiment, l_agents.get( "toy-node-1" ),
                                             p_passengerspeedatstationdistributionsupplier.get()
                                             )
            );

            l_messenger.experiment( l_experiment );

            // experiment (executable model)
            return l_experiment;

        }
        catch ( final Exception l_execption )
        {
            throw new CRuntimeException( l_execption );
        }
    }


    /**
     * creates the agent structure
     *
     * @param p_ai AI component
     * @return unmodifyable agent map
     */
    private static Map<String, String> agents( final Iagents p_ai )
    {
        return Collections.<String, String>unmodifiableMap(
                   p_ai.getAgents()
                       .getInstance()
                       .getAgent()
                       .parallelStream()
                       .collect( Collectors.toMap( Iagent::getId, i -> i.getConfiguration().getAsl() ) )
        );
    }

    /**
     * create the station list
     *
     * @param p_network network component
     * @param p_agents map with agent asl scripts
     * @param p_factory factory
     * @param p_time time reference
     * @param p_platforms map with already generated platforms
     * @return unmodifyable map with stations
     */
    private static Map<String, IStation<?>> station( final Network p_network, final Map<String, String> p_agents,
                                                     final IFactory p_factory, final ITime p_time, final Map<String, IPlatform<?>> p_platforms )
    {
        final Map<String, IElement.IGenerator<IStation<?>>> l_generators = new ConcurrentHashMap<>();
        final Set<IAction> l_actions = CCommon.actionsFromPackage().collect( Collectors.toSet() );
        final ListMultimap<String, IPlatform<?>> l_platformbystationid = Multimaps.index( p_platforms.values(), IPlatform::stationid );
        return Collections.<String, IStation<?>>unmodifiableMap(
                   p_network.getInfrastructure()
                            .getOperationControlPoints()
                            .getOcp()
                            .parallelStream()
                            .filter( i -> hasagentname( i.getAny() ) )
                            .map( i -> agentname( i, i.getAny() ) )
                            .map( i -> l_generators.computeIfAbsent(
                                        i.getRight(),
                                        a -> stationgenerator( p_factory, p_agents.get( i.getRight() ), l_actions, p_time )
                                        ).generatesingle(
                                            i.getLeft().getId(),
                                            i.getLeft().getGeoCoord().getCoord().get( 0 ),
                                            i.getLeft().getGeoCoord().getCoord().get( 1 ),
                                            l_platformbystationid.get( i.getLeft().getId() )
                                        )
                                )
                            .collect( Collectors.toMap( IElement::id, i -> i ) )
        );
    }


    /**
     * creates a station agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return station generator
     */
    private static IElement.IGenerator<IStation<?>> stationgenerator( final IFactory p_factory, final String p_asl,
                                                                      final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.station(
                IOUtils.toInputStream( p_asl, "UTF-8" ),
                p_actions,
                p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * create the platforms of all stations
     *
     * @param p_network network component
     * @param p_agents map with agent asl scripts
     * @param p_factory factory
     * @param p_time time reference
     * @return unmodifyable map with platforms
     */
    private static Map<String, IPlatform<?>> platform( final Network p_network, final Map<String, String> p_agents,
                                                                          final IFactory p_factory, final ITime p_time )
    {
        final Map<String, IElement.IGenerator<IPlatform<?>>> l_generators = new ConcurrentHashMap<>();
        final Set<IAction> l_actions = CCommon.actionsFromPackage().collect( Collectors.toSet() );
        return Collections.<String, IPlatform<?>>unmodifiableMap(
            p_network.getInfrastructure()
                     .getOperationControlPoints()
                     .getOcp()
                     .parallelStream()
                     .flatMap( ocp -> ocp.getAny().stream().filter( a -> a instanceof StationLayout )
                                         .findAny().map( a -> ( (StationLayout) a ).getPlatform().stream()
                                                                                       .map( p -> new ImmutablePair<EOcp, PlatformType>( ocp, p ) ) )
                                         .orElse( Stream.of() ) )
                     .filter( i -> i.getRight().getAgentRef() != null )
                     .map( i -> l_generators.computeIfAbsent(
                         i.getRight().getAgentRef().getAgent(),
                         a -> platformgenerator( p_factory, p_agents.get( i.getRight().getAgentRef().getAgent() ), l_actions, p_time )
                           ).generatesingle(
                         i.getLeft().getId() + "-track-" + i.getRight().getNumber(),
                         i.getLeft().getId()
                           )
                     )
                     .collect( Collectors.toMap( IElement::id, i -> i ) )
        );
    }


    /**
     * creates a platform agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return platform generator
     */
    private static IElement.IGenerator<IPlatform<?>> platformgenerator( final IFactory p_factory, final String p_asl,
                                                                        final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.platform(
                IOUtils.toInputStream( p_asl, "UTF-8" ),
                p_actions,
                p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * create the train list
     *
     * @param p_network network component
     * @param p_agents map with agent asl scripts
     * @param p_factory factory
     * @return unmodifiable map with trains
     */
    private static Pair<Map<String, ITrain<?>>, Map<String, IDoor<?>>> train( final Network p_network, final Map<String, String> p_agents,
                                                                              final IFactory p_factory, final ITime p_time )
    {
        final String l_dooragent = IStatefulElement.getDefaultAsl( "door" );
        final Map<String, IElement.IGenerator<ITrain<?>>> l_generators = new ConcurrentHashMap<>();
        final Set<IAction> l_actions = CCommon.actionsFromPackage().collect( Collectors.toSet() );
        final IElement.IGenerator<IDoor<?>> l_doorgenerator = doorgenerator( p_factory, l_dooragent, l_actions, p_time );
        final Map<String, AtomicLong> l_doorcount = Collections.synchronizedMap( new HashMap<>() );
        final Map<String, IDoor<?>> l_doors = Collections.synchronizedMap( new HashMap<>() );
        return new ImmutablePair<>( Collections.<String, ITrain<?>>unmodifiableMap(
            p_network.getTimetable()
                     .getTrains()
                     .getTrain()
                     .parallelStream()
                     .filter( i -> hasagentname( i.getAny3() ) )
                     .map( i -> agentname( i, i.getAny3() ) )
                     .map( i -> l_generators.computeIfAbsent(
                         i.getRight(),
                         a -> traingenerator( p_factory, p_agents.get( i.getRight() ), l_actions, p_time )
                         ).generatesingle(
                             i.getLeft().getId(),
                             i.getLeft()
                              .getTrainPartSequence()
                              .stream()
                              .flatMap( ref ->
                              {
                                  // @todo support multiple train parts
                                  final EOcpTT[] l_tts = ( (ETrainPart) ref.getTrainPartRef().get( 0 ).getRef() )
                                      .getOcpsTT().getOcpTT().toArray( new EOcpTT[0] );
                                  final CTrain.CTimetableEntry[] l_entries = new CTrain.CTimetableEntry[l_tts.length];
                                  for ( int j = 0; j < l_tts.length; j++ )
                                  {
                                      final EArrivalDepartureTimes l_times = l_tts[j].getTimes().stream()
                                                                             .filter( t -> t.getScope().equalsIgnoreCase( "published" ) )
                                                                             .findAny()
                                                                             .orElseThrow( () -> new CSemanticException( "missing published times" ) );
                                      l_entries[j] = new CTrain.CTimetableEntry(
                                          j < 1 ? 0.0 : ( (ETrack) l_tts[j - 1].getSectionTT().getTrackRef().get( 0 ).getRef() ).getTrackTopology()
                                                                                                                                .getTrackEnd().getPos()
                                                                                                                                .doubleValue(),
                                          ( (EOcp) l_tts[j].getOcpRef() ).getId(),
                                          l_tts[j].getStopDescription().getOtherAttributes().getOrDefault( PLATFORM_REF_ATTRIBUTE, null ),
                                          l_times.getArrival() == null
                                                  ? null
                                                  : l_times.getArrival().toGregorianCalendar().toZonedDateTime()
                                                    .with( LocalDate.now() ).toInstant(),
                                          l_times.getDeparture() == null
                                                  ? null
                                                  : l_times.getDeparture().toGregorianCalendar().toZonedDateTime()
                                                    .with( LocalDate.now() ).toInstant()
                                      );
                                  }
                                  return Arrays.stream( l_entries );
                              } ),
                             i.getLeft()
                              .getTrainPartSequence()
                              .stream()
                              // @todo support multiple train parts
                              .map( s -> (ETrainPart) s.getTrainPartRef().get( 0 ).getRef() )
                              .map( p -> (EFormation) p.getFormationTT().getFormationRef() )
                              .flatMap( f -> f.getTrainOrder().getVehicleRef().stream() )
                              .map( r -> new ImmutablePair<BigInteger, TDoors>( r.getVehicleCount(),
                                                                                ( (EVehicle) r.getVehicleRef() ).getWagon().getPassenger().getDoors() ) )
                              .flatMap( v -> IntStream.range( 0, v.getLeft().intValue() * v.getRight().getNumber().intValue() )
                                                      .mapToObj( j -> l_doors.computeIfAbsent( "door-" + i.getLeft().getId() + "-"
                                                                                               + l_doorcount.computeIfAbsent(
                                                                                                   i.getLeft().getId(),
                                                                                                   id -> new AtomicLong( 1L ) ).getAndIncrement(),
                                                          id -> l_doorgenerator.generatesingle( id, i.getLeft().getId(),
                                                                                                v.getRight().getEntranceWidth().doubleValue()
                                                                                                / v.getRight().getNumber().longValue() )
                                                      )
                                                      )
                              )
                             .collect( Collectors.toList() )
                         )
                     )
                     .collect( Collectors.toMap( IElement::id, i -> i ) )
        ), l_doors );
    }


    /**
     * creates a train agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return train generator
     */
    private static IElement.IGenerator<ITrain<?>> traingenerator( final IFactory p_factory, final String p_asl,
                                                                  final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.train(
                IOUtils.toInputStream( p_asl, "UTF-8" ),
                p_actions,
                p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * creates a train door agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return train door generator
     */
    private static IElement.IGenerator<IDoor<?>> doorgenerator( final IFactory p_factory, final String p_asl,
                                                                final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.door(
                IOUtils.toInputStream( p_asl, "UTF-8" ),
                p_actions,
                p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * creates a PassengerSource agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return PassengerSource generator
     */
    private static IElement.IGenerator<IPassengerSource<?>> passengersourcegenerator(
            final IFactory p_factory, final String p_asl, final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.passengersource(
                    IOUtils.toInputStream( p_asl, "UTF-8" ),
                    p_actions,
                    p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * creates a Passenger agent generator
     *
     * @param p_factory factory
     * @param p_asl asl script as String
     * @param p_actions actions
     * @return Passenger generator
     */
    private static IElement.IGenerator<IPassenger<?>> passengergenerator(
            final IFactory p_factory, final String p_asl, final Set<IAction> p_actions, final ITime p_time )
    {
        try
        {
            return p_factory.passenger(
                    IOUtils.toInputStream( p_asl, "UTF-8" ),
                    p_actions,
                    p_time
            );
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }


    /**
     * get an agent-reference name
     *
     * @param p_value value of the agent-reference
     * @param p_list object list
     * @return agent-reference name
     * @throws CNotFoundException is thrown on not found
     */
    @SuppressWarnings( "unchecked" )
    private static <T> Pair<T, String> agentname( final T p_value, final List<Object> p_list )
    {
        return new ImmutablePair<T, String>(
            p_value,
            p_list.parallelStream()
                  .filter( a -> a instanceof AgentRef )
                  .findAny()
                  .map( a -> (AgentRef) a )
                  .map( AgentRef::getAgent )
                  .orElseThrow( () -> new CNotFoundException( CCommon.languagestring( CXMLReader.class, "agentreferencenotfound" ) ) )
        );
    }

    /**
     * filter an object list
     *
     * @param p_list object list
     * @return agent-reference exist
     */
    private static boolean hasagentname( final List<Object> p_list )
    {
        return p_list.parallelStream()
                     .anyMatch( a -> a instanceof AgentRef );
    }

}
