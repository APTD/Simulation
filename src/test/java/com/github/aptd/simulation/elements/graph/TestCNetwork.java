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

package com.github.aptd.simulation.elements.graph;

import com.github.aptd.simulation.IBaseTest;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.local.CNetwork;
import com.github.aptd.simulation.elements.graph.network.local.CTrack;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.graph.network.INode;
import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * network test
 */
public final class TestCNetwork extends IBaseTest
{
    /**
     * station generator
     */
    private IElement.IGenerator<?> m_station;
    /**
     * track generator
     */
    private IElement.IGenerator<?> m_track;


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
            m_station = new CStation.CGenerator( l_station, CConfiguration.INSTANCE.agentaction(), CConfiguration.INSTANCE.agentaggregation() );
            m_track = new CTrack.CGenerator( IOUtils.toInputStream( "", "UTF-8" ), CConfiguration.INSTANCE.agentaction(), CConfiguration.INSTANCE.agentaggregation() );
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
            new CNetwork(
/*
                Stream.of(
                    m_station.generatesingle( l_goe, 51.536777, 9.926074 ),
                    m_station.generatesingle( l_kreiensen, 51.850591, 9.969346 ),
                    m_station.generatesingle( "Herzberg Harz", 51.644046, 10.329508 ),
                    m_station.generatesingle( "Heilbad Heiligenstadt", 51.377105, 10.123940 ),

                    m_station.generatesingle( "Alfeld (Leine)", 51.984547, 9.812833 ),
                    m_station.generatesingle( "Goslar", 51.911861, 10.420842 ),

                    m_station.generatesingle( "Hann Münden", 51.412148, 9.657186 ),
                    m_station.generatesingle( "Witzenhausen", 51.351333, 9.860542 )
                ).collect( Collectors.toSet() ),
*/
                Stream.of(

                    m_track.generatesingle( l_goe, "Hann Münden" ),
                    m_track.generatesingle( l_goe, "Witzenhausen" ),
                    m_track.generatesingle( l_goe, "Kreiensen" ),
                    m_track.generatesingle( l_goe, "Heilbad Heiligenstadt" ),

                    m_track.generatesingle( l_kreiensen, l_goe ),
                    m_track.generatesingle( l_kreiensen, "Goslar" ),
                    m_track.generatesingle( l_kreiensen, "Alfeld (Leine)" ),
                    m_track.generatesingle( l_kreiensen, "Herzberg Harz" ),

                    m_track.generatesingle( "Herzberg Harz", "Heilbad Heiligenstadt" ),
                    m_track.generatesingle( "Herzberg Harz", l_kreiensen ),

                    m_track.generatesingle( "Heilbad Heiligenstadt", "Herzberg Harz" ),
                    m_track.generatesingle( "Heilbad Heiligenstadt", l_goe ),

                    m_track.generatesingle( "Alfeld (Leine)", l_kreiensen ),

                    m_track.generatesingle( "Goslar", l_kreiensen ),

                    m_track.generatesingle( "Hann Münden", l_goe ),

                    m_track.generatesingle( "Witzenhausen", l_goe )

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
        new TestCNetwork().invoketest();
    }




}
