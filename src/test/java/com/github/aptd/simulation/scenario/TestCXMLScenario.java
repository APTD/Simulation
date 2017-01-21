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

import com.github.aptd.simulation.elements.factory.local.CNetworkEdge;
import com.github.aptd.simulation.elements.factory.local.CSparseGraph;
import com.github.aptd.simulation.elements.factory.local.CStation;
import com.github.aptd.simulation.scenario.generator.CStationGenerator;
import com.github.aptd.simulation.scenario.model.graph.network.INetworkEdge;
import com.github.aptd.simulation.scenario.model.graph.network.INetworkNode;
import com.github.aptd.simulation.scenario.reader.CXMLReader;
import com.github.aptd.simulation.scenario.xml.Asimov;
import com.github.aptd.simulation.scenario.xml.Iagent;
import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


/**
 * scenario XML test
 */
public final class TestCXMLScenario
{
    /**
     * configuration object
     */
    private Asimov m_scenario;
    /**
     * agent configuration data
     */
    private Map<String, String> m_agent;

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
            .collect( Collectors.toMap( Iagent::getId, i -> i.getConfiguration().getAsl() ) );

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
            new CSparseGraph<String, INetworkNode<String>, INetworkEdge<String>>(
                m_scenario
                    .getNetwork()
                    .getInfrastructure()
                    .getOperationControlPoints()
                    .getOcp()
                    .parallelStream()
                    .map( i -> {
                        try
                        {
                            return new CStationGenerator<String>(
                                IOUtils.toInputStream( m_agent.get( i.getId() ), "UTF-8" ),
                                CStation.class
                            ).generatesingle( i.getDescription(), i.getGeoCoord().getCoord().get( 0 ), i.getGeoCoord().getCoord().get( 1 ) );
                        }
                        catch ( final Exception l_exception )
                        {
                            assertTrue( l_exception.getMessage(), false );
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
                    .map( i -> CNetworkEdge.from(
                                    i.getTrackTopology().getTrackBegin().getMacroscopicNode().getOcpRef(),
                                    i.getTrackTopology().getTrackEnd().getMacroscopicNode().getOcpRef()
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

        final TestCXMLScenario l_test = new TestCXMLScenario();

        l_test.initialize();
        l_test.testNetworkWithAgent();
    }
}
