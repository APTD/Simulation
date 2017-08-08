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
import com.github.aptd.simulation.core.statistic.IStatistic;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.core.time.local.CStepTime;
import com.github.aptd.simulation.datamodel.xml.AgentRef;
import com.github.aptd.simulation.datamodel.xml.Asimov;
import com.github.aptd.simulation.datamodel.xml.Iagent;
import com.github.aptd.simulation.datamodel.xml.Iagents;
import com.github.aptd.simulation.datamodel.xml.Network;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.passenger.IPassengerSource;
import com.github.aptd.simulation.elements.train.CTrain;
import com.github.aptd.simulation.elements.train.ITrain;
import com.github.aptd.simulation.error.CNotFoundException;
import com.github.aptd.simulation.error.CRuntimeException;
import com.github.aptd.simulation.error.CSemanticException;
import com.github.aptd.simulation.factory.IFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.common.CCommon;
import org.railml.schemas._2016.EArrivalDepartureTimes;
import org.railml.schemas._2016.EOcp;
import org.railml.schemas._2016.EOcpTT;
import org.railml.schemas._2016.ETrack;
import org.railml.schemas._2016.ETrainPart;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.time.Duration;
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
import java.util.stream.Collectors;


/**
 * data-model XML reader
 *
 * @bug incomplete
 */
public final class CXMLReader implements IDataModel
{
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
            m_context = JAXBContext.newInstance( Asimov.class, AgentRef.class );
        }
        catch ( final JAXBException l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IExperiment get( final IFactory p_factory, final String p_datamodel,
                                  final long p_simulationsteps, final boolean p_parallel )
    {
        try
        (
            final FileInputStream l_stream = new FileInputStream( p_datamodel )
        )
        {
            final Asimov l_model = (Asimov) m_context.createUnmarshaller().unmarshal( l_stream );

            // time definition
            final ITime l_time = new CStepTime(
                    ZonedDateTime.now( ZoneId.systemDefault() )
                            .with( ChronoField.CLOCK_HOUR_OF_DAY, 9 )
                            .with( ChronoField.MINUTE_OF_HOUR, 45 )
                            .toInstant(),
                    Duration.ofSeconds( 1 ) );

            // asl agent definition
            final Map<String, String> l_agentdefs = agents( l_model.getAi() );

            // macro (train-network) and microscopic model
            final Map<String, IStation<?>> l_station = station( l_model.getNetwork(), l_agentdefs, p_factory, l_time );
            final Map<String, ITrain<?>> l_train = train( l_model.getNetwork(), l_agentdefs, p_factory, l_time );

            final Map<String, IElement<?>> l_agents = new HashMap<>();
            l_agents.putAll( l_station );
            l_agents.putAll( l_train );

            // @todo create passengersources according to scenario definition
            l_agents.put( "passengersource_test",
                    passengersourcegenerator(
                            p_factory, "                            !main.\n"
                                    + "\n"
                                    + "                            +!main <-\n"
                                    + "                            generic/print(\"hello passenger source\").\n"
                                    + "+!activate <-\n    state/timertransition\n.",
                            CCommon.actionsFromPackage().collect( Collectors.toSet() ),
                            l_time )
                            .generatesingle( new UniformRealDistribution( 0.0, 1800000.0 ),
                                    l_time.current().toEpochMilli(), 20 )
            );

            // experiment (executable model)
            return new CExperiment( p_simulationsteps, p_parallel, IStatistic.EMPTY, l_agents, l_time );

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
     * @param p_agents map with agents
     * @param p_factory factory
     * @param p_time time reference
     * @return unmodifyable map with stations
     */
    private static Map<String, IStation<?>> station( final Network p_network, final Map<String, String> p_agents,
                                                     final IFactory p_factory, final ITime p_time )
    {
        final Map<String, IElement.IGenerator<IStation<?>>> l_generators = new ConcurrentHashMap<>();
        final Set<IAction> l_actions = CCommon.actionsFromPackage().collect( Collectors.toSet() );
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
                                            i.getLeft().getDescription(),
                                            i.getLeft().getGeoCoord().getCoord().get( 0 ),
                                            i.getLeft().getGeoCoord().getCoord().get( 1 )
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
     * create the train list
     *
     * @param p_network network component
     * @param p_agents map with agents
     * @param p_factory factory
     * @return unmodifiable map with trains
     */
    private static Map<String, ITrain<?>> train( final Network p_network, final Map<String, String> p_agents, final IFactory p_factory, final ITime p_time )
    {
        final Map<String, IElement.IGenerator<ITrain<?>>> l_generators = new ConcurrentHashMap<>();
        final Set<IAction> l_actions = CCommon.actionsFromPackage().collect( Collectors.toSet() );
        return Collections.<String, ITrain<?>>unmodifiableMap(
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
                              } )
                         )
                     )
                     .collect( Collectors.toMap( IElement::id, i -> i ) )
        );
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
