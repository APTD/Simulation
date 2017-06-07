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
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.error.CNotFoundException;
import com.github.aptd.simulation.error.CRuntimeException;
import com.github.aptd.simulation.error.CSemanticException;
import com.github.aptd.simulation.factory.IFactory;
import com.github.aptd.simulation.datamodel.xml.AgentRef;
import com.github.aptd.simulation.datamodel.xml.Asimov;
import com.github.aptd.simulation.datamodel.xml.Iagent;
import com.github.aptd.simulation.datamodel.xml.Iagents;
import com.github.aptd.simulation.datamodel.xml.Network;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.common.CCommon;
import org.railml.schemas._2016.EOcp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * data-model XML reader
 *
 * @bug incomplete
 */
public final class CXMLReader implements IDataModel
{

    /**
     * ctor
     * @throws JAXBException is thrown on any jaxb exception
     */
    @SuppressWarnings( "unchecked" )
    private CXMLReader( final InputStream p_stream ) throws JAXBException
    {
        final JAXBContext l_context = JAXBContext.newInstance( Asimov.class, AgentRef.class );
        final Asimov l_model = (Asimov) l_context.createUnmarshaller().unmarshal( p_stream );

        final Map<String, String> l_agents = agents( l_model.getAi() );
        final Map<String, IStation<?>> l_station = station( l_model.getNetwork(), l_agents );
    }

    /**
     * factory
     *
     * @param p_stream input XML stream
     * @return data-model
     */
    public static IDataModel from( final InputStream p_stream )
    {
        try
        {
            return new CXMLReader( p_stream );
        }
        catch ( final JAXBException l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
    }

    @Override
    public final IExperiment get( final IFactory p_factory )
    {
        return null;
    }


    /**
     * creates the agent structure
     *
     * @param p_ai AI component
     * @return unmodifyable agent map
     */
    private static Map<String, String> agents( final Iagents p_ai )
    {
        return Collections.unmodifiableMap(
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
     * @return unmodifyable map with stations
     */
    private static Map<String, IStation<?>> station( final Network p_network, final Map<String, String> p_agents )
    {
        return Collections.unmodifiableMap(
                   p_network.getInfrastructure()
                            .getOperationControlPoints()
                            .getOcp()
                            .parallelStream()
                            .filter( i -> hasagentname( i.getAny() ) )
                            .map( i -> agentname( i, i.getAny() ) )
                            .map( i -> station( i, p_agents ) )
                            .collect( Collectors.toMap( IElement::id, i -> i ) )
        );

    }


    /**
     * creates a station agent
     *
     * @param p_station station reference
     * @param p_agents agent map
     * @return station
     * @todo replace with factory
     */
    private static IStation<?> station( final Pair<EOcp, String> p_station, final Map<String, String> p_agents )
    {
        try
        {
            return new CStation.CGenerator(
                IOUtils.toInputStream(  p_agents.get( p_station.getRight() ), "UTF-8" ),
                CCommon.actionsFromPackage().collect( Collectors.toSet() )
            ).generatesingle(
                p_station.getLeft().getDescription(),
                p_station.getLeft().getGeoCoord().getCoord().get( 0 ),
                p_station.getLeft().getGeoCoord().getCoord().get( 1 )
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
