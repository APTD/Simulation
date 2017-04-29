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

import com.codepoetics.protonpack.StreamUtils;
import com.github.aptd.simulation.IBaseTest;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.graph.network.local.CNetwork;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.elements.graph.network.local.CTrack;
import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
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
    private IElement.IGenerator<IStation<?>> m_station;
    /**
     * track generator
     */
    private IElement.IGenerator<ITrack<?>> m_track;


    /**
     * initialize test
     */
    @Before
    public void ininitialize()
    {
        CConfiguration.INSTANCE.loadstring( "" );
        try
        (
            final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
        )
        {
            m_station = new CStation.CGenerator(
                l_station,
                CConfiguration.INSTANCE.agentaction(),
                CConfiguration.INSTANCE.agentaggregation()
            );

            m_track = new CTrack.CGenerator(
                IOUtils.toInputStream( "", "UTF-8" ),
                CConfiguration.INSTANCE.agentaction(),
                CConfiguration.INSTANCE.agentaggregation()
            );
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
    public final void networkbuild()
    {
        Assume.assumeNotNull( m_station );
        Assume.assumeNotNull( m_track );

        final Map<String, IStation<?>> l_station = StreamUtils.windowed(
                                                       Stream.of(
                                                           "Göttingen", 51.536777, 9.926074,
                                                           "Kreiensen", 51.850591, 9.969346,
                                                           "Herzberg Harz", 51.644046, 10.329508,
                                                           "Heilbad Heiligenstadt", 51.377105, 10.123940,

                                                           "Alfeld (Leine)", 51.984547, 9.812833,
                                                           "Goslar", 51.911861, 10.420842,

                                                           "Hann Münden", 51.412148, 9.657186,
                                                           "Witzenhausen", 51.351333, 9.860542
                                                       ),
                                                    3,
                                                    3
        )
            .map( i -> m_station.generatesingle( i.get( 0 ), i.get( 1 ), i.get( 2 ) ) )
            .collect( Collectors.toMap( IElement::id, i -> i ) );


        final IStation<?> l_goettingen = l_station.get( "Göttingen" );
        final IStation<?> l_kreiensen = l_station.get( "Kreiensen" );


        // build 8 node mini scenario, one full-qualified station with
        // different platforms and levels all other only transit station
        System.out.println(
            new CNetwork(

                Stream.of(

                    m_track.generatesingle(  "Track 01", l_goettingen, l_station.get( "Hann Münden" ) ),
                    m_track.generatesingle(  "Track 02", l_goettingen, l_station.get( "Witzenhausen" ) ),
                    m_track.generatesingle(  "Track 03", l_goettingen, l_kreiensen ),
                    m_track.generatesingle(  "Track 04", l_goettingen, l_station.get( "Heilbad Heiligenstadt" ) ),

                    m_track.generatesingle(  "Track 05", l_kreiensen, l_goettingen ),
                    m_track.generatesingle(  "Track 06", l_kreiensen, l_station.get( "Goslar" ) ),
                    m_track.generatesingle(  "Track 07", l_kreiensen, l_station.get( "Alfeld (Leine)" ) ),
                    m_track.generatesingle(  "Track 08", l_kreiensen, l_station.get( "Herzberg Harz" ) ),

                    m_track.generatesingle(  "Track 09", l_station.get( "Herzberg Harz" ), l_station.get( "Heilbad Heiligenstadt" ) ),
                    m_track.generatesingle(  "Track 10", l_station.get( "Herzberg Harz" ), l_kreiensen ),

                    m_track.generatesingle(  "Track 11", l_station.get( "Heilbad Heiligenstadt" ), l_station.get( "Herzberg Harz" ) ),
                    m_track.generatesingle(  "Track 12", l_station.get( "Heilbad Heiligenstadt" ), l_goettingen ),

                    m_track.generatesingle(  "Track 13", l_station.get( "Alfeld (Leine)" ), l_kreiensen ),

                    m_track.generatesingle(  "Track 14", l_station.get( "Goslar" ), l_kreiensen ),

                    m_track.generatesingle(  "Track 15", l_station.get( "Hann Münden" ), l_goettingen ),

                    m_track.generatesingle(  "Track 16", l_station.get( "Witzenhausen" ), l_goettingen )

                )
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
