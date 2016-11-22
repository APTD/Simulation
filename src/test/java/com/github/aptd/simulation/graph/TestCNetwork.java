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
            m_station = new CStationGenerator<String>( l_station, null, String.class );
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

        // build 8 node mini scenario, one full-qualified station with
        // different platforms and levels all other only transit station

        /*
        new CSparseGraph<String, INetworkNode<String>, INetworkEdge<String>>(
            Stream.of(
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
    private static class CStationGenerator<T> extends IBaseAgentGenerator<INetworkNode<T>>
    {
        /**
         * class of template parameter
         */
        private final Class<T> m_classid;
        /**
         * generator class
         */
        private final Class<? extends INetworkNode<T>> m_classgenerator;

        /**
         * ctor
         *
         * @param p_stream asl input stream
         * @param p_classgenerator generator class
         * @param p_classid node identifier type class
         * @throws Exception thrown on any parsing exception
         */
        public CStationGenerator( final InputStream p_stream, final Class<? extends INetworkNode<T>> p_classgenerator, final Class<T> p_classid ) throws Exception
        {
            super( p_stream, CCommon.actionsFromPackage().collect( Collectors.toSet() ), IAggregation.EMPTY );
            m_classgenerator = p_classgenerator;
            m_classid = p_classid;
        }

        @Override
        public INetworkNode<T> generatesingle( final Object... p_data )
        {
            try
            {
                return m_classgenerator
                    .getConstructor( IAgentConfiguration.class, m_classid, Double.class, Double.class )
                    .newInstance( m_configuration, p_data[0], p_data[1], p_data[2] );
            }
            catch ( final NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException l_exception )
            {
                throw new CSemanticException( l_exception );
            }
        }
    }


}
