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

package com.github.aptd.simulation.scenario;

import com.github.aptd.simulation.IBaseTest;
import com.github.aptd.simulation.elements.graph.network.INode;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.graph.network.local.CNetwork;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.elements.graph.network.local.CTrack;
import com.github.aptd.simulation.scenario.xml.AgentRef;
import com.github.aptd.simulation.scenario.xml.Asimov;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.language.score.IAggregation;
import org.railml.schemas._2016.EOcp;
import scenario.CXMLReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * scenario XML test
 */
public final class TestCXMLScenario extends IBaseTest
{
    /**
     * configuration object
     */
    private Asimov m_scenario;
    /**
     * agent configuration data
     */
    private Map<String, ImmutablePair<Class<? extends IAgent<?>>, String>> m_agent;

    /**
     * reads a test scenario
     */
    @Before
    public final void initialize()
    {
        try
            (
                final InputStream l_stream = new FileInputStream( "src/test/resources/scenario.xml" );
            )
        {

            m_scenario = new CXMLReader().get( l_stream );
        }
        catch ( final Exception l_exception )
        {
            assertTrue( l_exception.getMessage(), false );
        }

        // read all agents
        m_agent = m_scenario
            .getAi()
            .getAgents()
            .getInstance()
            .getAgent()
            .parallelStream()
            .collect( Collectors./*<Iagent, String, ImmutablePair<Class<? extends IAgent<?>>, String>>*/toMap(
                i -> i.getId(),
                i ->
                {
                    Class<? extends IAgent<?>> l_clazz = null;
                    try
                    {
                        l_clazz = (Class<? extends IAgent<?>>) Class.forName( i.getJavaClass() );
                    }
                    catch ( final ClassNotFoundException l_exception )
                    {
                        fail( "ClassNotFoundException: " + i.getJavaClass() );
                    }
                    return new ImmutablePair<>( l_clazz, i.getConfiguration().getAsl() );
                }
            ) );

        System.out.println( m_agent );

    }


    /**
     * test graph build with node agents
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public final void testNetworkWithAgent()
    {
        Assume.assumeNotNull( m_scenario );
        Assume.assumeNotNull( m_agent );

        System.out.println(
            new CNetwork<EOcp, INode<EOcp>, ITrack<EOcp>>(
                m_scenario
                    .getNetwork()
                    .getInfrastructure()
                    .getOperationControlPoints()
                    .getOcp()
                    .parallelStream()
                    .map( i -> {
                        try
                        {
                            final AgentRef l_agentref = i.getAny().stream()
                                                       .filter( a -> a instanceof AgentRef )
                                                       .map( a -> (AgentRef) a )
                                                       .findAny()
                                                       .orElseThrow( () -> new RuntimeException( "no agentRef on ocp " + i.getId() ) );

                            new CStation.CGenerator(
                                IOUtils.toInputStream( m_agent.get( l_agentref.getAgent() ).getRight(), "UTF-8" ),
                                CCommon.actionsFromPackage().collect( Collectors.toSet() ),
                                IAggregation.EMPTY
                            ).generatesingle( i.getDescription(), i.getGeoCoord().getCoord().get( 0 ), i.getGeoCoord().getCoord().get( 1 ) );
                        }
                        catch ( final Exception l_exception )
                        {
                            l_exception.printStackTrace();
                            fail( l_exception.getMessage() );
                            return null;
                        }
                    } )
                    .filter( Objects::nonNull )
                    .collect( Collectors.toSet() ),


                m_scenario
                    .getNetwork()
                    .getInfrastructure()
                    .getTracks()
                    .getTrack()
                    .parallelStream()
                    .map( i -> CTrack.from(
                        (EOcp) i.getTrackTopology().getTrackBegin().getMacroscopicNode().getOcpRef(),
                        (EOcp) i.getTrackTopology().getTrackEnd().getMacroscopicNode().getOcpRef()
                    ) )
                    .collect( Collectors.toSet() )

            )
        );
    }


    /**
     * run manual test
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCXMLScenario().invoketest();
    }
}
