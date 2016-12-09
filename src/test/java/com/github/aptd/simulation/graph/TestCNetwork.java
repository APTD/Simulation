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

package com.github.aptd.simulation.graph;

import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.elements.graph.local.CNetworkEdge;
import com.github.aptd.simulation.elements.graph.local.CSparseGraph;
import com.github.aptd.simulation.scenario.generator.CStation;
import com.github.aptd.simulation.elements.graph.network.INetworkEdge;
import com.github.aptd.simulation.elements.graph.network.INetworkNode;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * network test
 */
public final class TestCNetwork
{
    /**
     * station generator
     */
    private IBaseAgentGenerator<INetworkNode<String>> m_station;

    /**
     * initialize test
     */
    @Before
    @SuppressWarnings( "unchecked" )
    public void ininitialize()
    {
        CConfiguration.INSTANCE.loadstring( "" );
        try
        (
            final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
        )
        {
            m_station = new CStation<>( l_station, com.github.aptd.simulation.elements.graph.local.CStation.class );
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
        }
    }

    /**
     * network design test
     */
    @Test
    public void networkbuild()
    {
        Assume.assumeNotNull( m_station );

        final String l_goe = "Göttingen";
        final String l_kreiensen = "Kreiensen";

        // build 8 node mini scenario, one full-qualified station with
        // different platforms and levels all other only transit station
        System.out.println(
            new CSparseGraph<String, INetworkNode<String>, INetworkEdge<String>>(

                Stream.of(
                    m_station.generatesingle( l_goe, 51.536777, 9.926074 ),
                    m_station.generatesingle( l_kreiensen, 51.850591, 9.969346 ),
                    m_station.generatesingle( "Herzberg Harz", 51.644046, 10.329508 ),
                    m_station.generatesingle( "Heilbad Heiligenstadt", 51.377105, 10.123940 ),

                    m_station.generatesingle( "Alfred (Leine)", 51.984547, 9.812833 ),
                    m_station.generatesingle( "Goslar", 51.911861, 10.420842 ),

                    m_station.generatesingle( "Hann Münden", 51.412148, 9.657186 ),
                    m_station.generatesingle( "Witzenhausen", 51.351333, 9.860542 )
                ).collect( Collectors.toSet() ),

                Stream.of(

                    CNetworkEdge.from( l_goe, "Hann Münden" ),
                    CNetworkEdge.from( l_goe, "Witzenhausen" ),
                    CNetworkEdge.from( l_goe, "Kreiensen" ),
                    CNetworkEdge.from( l_goe, "Heilbad Heiligenstadt" ),

                    CNetworkEdge.from( l_kreiensen, l_goe ),
                    CNetworkEdge.from( l_kreiensen, "Goslar" ),
                    CNetworkEdge.from( l_kreiensen, "Alfred (Leine)" ),
                    CNetworkEdge.from( l_kreiensen, "Herzberg Harz" ),

                    CNetworkEdge.from( "Herzberg Harz", "Heilbad Heiligenstadt" ),
                    CNetworkEdge.from( "Herzberg Harz", l_kreiensen ),

                    CNetworkEdge.from( "Heilbad Heiligenstadt", "Herzberg Harz" ),
                    CNetworkEdge.from( "Heilbad Heiligenstadt", l_goe ),

                    CNetworkEdge.from( "Alfred (Leine)", l_kreiensen ),

                    CNetworkEdge.from( "Goslar", l_kreiensen ),

                    CNetworkEdge.from( "Hann Münden", l_goe ),

                    CNetworkEdge.from( "Witzenhausen", l_goe )

                ).collect( Collectors.toSet() )
            )
        );
    }

    /**
     * main method to run test manual
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        final TestCNetwork l_networktest = new TestCNetwork();
        l_networktest.ininitialize();
        l_networktest.networkbuild();
    }




}
