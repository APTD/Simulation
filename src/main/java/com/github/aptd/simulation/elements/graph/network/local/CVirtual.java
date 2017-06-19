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

package com.github.aptd.simulation.elements.graph.network.local;

import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.graph.network.IStation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * virtual node
 */
public final class CVirtual extends IBaseStation
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -7103541058979258246L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "virtual";

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id station identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     * @param p_time time reference
     */
    private CVirtual(
        final IAgentConfiguration<IStation<?>> p_configuration,
        final String p_id,
        final double p_longitude,
        final double p_latitude,
        final ITime p_time
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_longitude, p_latitude, p_time );
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IStation<?>>
    {

        /**
         * ctor
         *
         * @param p_stream stream
         * @param p_actions action
         * @param p_time time reference
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final ITime p_time ) throws Exception
        {
            super( p_stream, p_actions, CStation.class, p_time );
        }

        @Override
        protected final Pair<IStation<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CVirtual( m_configuration, p_data[0].toString(), (double) p_data[1], (double) p_data[1], m_time ),
                Stream.of( FUNCTOR, BASEFUNCTOR )
            );
        }
    }

}
