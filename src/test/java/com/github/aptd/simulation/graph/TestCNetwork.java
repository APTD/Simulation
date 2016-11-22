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

import com.github.aptd.simulation.simulation.error.CSemanticException;
import com.github.aptd.simulation.simulation.graph.local.CNetworkEdge;
import com.github.aptd.simulation.simulation.graph.local.CSparseGraph;
import com.github.aptd.simulation.simulation.graph.local.CStation;
import com.github.aptd.simulation.simulation.graph.network.INetworkEdge;
import com.github.aptd.simulation.simulation.graph.network.INetworkNode;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        try
        (
            final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
        )
        {
            m_station = new CStationGenerator<>( l_station, CStation.class );
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

        // build 8 node mini scenario, one full-qualified station with
        // different platforms and levels all other only transit station
        System.out.println(
            new CSparseGraph<String, INetworkNode<String>, INetworkEdge<String>>(

                Stream.of(
                    m_station.generatesingle( "Göttingen", 51.536777, 9.926074 ),
                    m_station.generatesingle( "Kreiensen", 51.850591, 9.969346 ),
                    m_station.generatesingle( "Herzberg Harz", 51.644046, 10.329508 ),
                    m_station.generatesingle( "Heilbad Heiligenstadt", 51.377105, 10.123940 ),

                    m_station.generatesingle( "Alfred (Leine)", 51.984547, 9.812833 ),
                    m_station.generatesingle( "Goslar", 51.911861, 10.420842 ),

                    m_station.generatesingle( "Hann Münden", 51.412148, 9.657186 ),
                    m_station.generatesingle( "Witzenhausen", 51.351333, 9.860542 )
                ).collect( Collectors.toSet() ),

                Stream.of(

                    CNetworkEdge.from( "Göttingen", "Kreiensen" ),
                    CNetworkEdge.from( "Kreiensen", "Herzberg Harz" ),
                    CNetworkEdge.from( "Herzberg Harz", "Heilbad Heiligenstadt" ),

                    CNetworkEdge.from( "Kreiensen", "Alfred (Leine)" ),
                    CNetworkEdge.from( "Kreiensen", "Goslar" ),

                    CNetworkEdge.from( "Göttingen", "Hann Münden" ),
                    CNetworkEdge.from( "Göttingen", "Witzenhausen" )

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

    /**
     * generator for full-qualified stations
     * s
     * @tparam T node identifier
     */
    private static final class CStationGenerator<T, G extends INetworkNode<T>> extends IBaseAgentGenerator<INetworkNode<T>>
    {
        /**
         * ctor reference
         */
        private final Constructor<G> m_ctor;

        /**
         * ctor
         *
         * @param p_stream asl input stream
         * @param p_classgenerator generator class
         * @throws Exception thrown on any parsing exception
         */
        public CStationGenerator( final InputStream p_stream, final Class<G> p_classgenerator ) throws Exception
        {
            super( p_stream, CCommon.actionsFromPackage().collect( Collectors.toSet() ), IAggregation.EMPTY );
            m_ctor = p_classgenerator.getConstructor( IAgentConfiguration.class, Object.class, double.class, double.class );
        }

        @Override
        public final INetworkNode<T> generatesingle( final Object... p_data )
        {
            try
            {
                return m_ctor.newInstance( m_configuration, p_data[0], p_data[1], p_data[2] );
            }
            catch ( final IllegalAccessException | InvocationTargetException | InstantiationException l_exception )
            {
                throw new CSemanticException( l_exception );
            }
        }
    }


}
