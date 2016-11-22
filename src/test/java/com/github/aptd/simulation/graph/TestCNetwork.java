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
import com.github.aptd.simulation.simulation.graph.local.CStation;
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
    public void ininitialize()
    {
        try
        (
            final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
        )
        {
            m_station = new CStationGenerator<>( l_station, CStation.class, String.class );
        }
        catch ( final Exception l_exception )
        {
        }
    }

    /**
     * network design test
     */
    @Test
    public void networkbuild()
    {
        Assume.assumeNotNull( m_station );

        //m_station.generatesingle( "Göttingen", 51.536777, 9.926074 );


        // build 8 node mini scenario, one full-qualified station with
        // different platforms and levels all other only transit station

        /*
        new CSparseGraph<String, INetworkNode<String>, INetworkEdge<String>>(
            Stream.of(
            Göttingen 51.536777, 9.926074 -> Heilbad Heiligenstadt 51.377105, 10.123940
            Kreiensen 51.850591, 9.969346  -> Herzberg Harz 51.644046, 10.329508

            Goslar 51.911861, 10.420842
            Alfred (Leine) 51.984547, 9.812833

            Hann Münden 51.412148, 9.657186
            Witzenhausen 51.351333, 9.860542
            ).collect( Collectors.toSet() ),
            Stream.of().collect( Collectors.toSet() )
        );
        */
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
         * @param p_classid node identifier type class
         * @throws Exception thrown on any parsing exception
         */
        public CStationGenerator( final InputStream p_stream, final Class<G> p_classgenerator, final Class<T> p_classid ) throws Exception
        {
            super( p_stream, CCommon.actionsFromPackage().collect( Collectors.toSet() ), IAggregation.EMPTY );
            m_ctor = p_classgenerator.getConstructor( IAgentConfiguration.class, p_classid, Double.class, Double.class );
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
