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
import com.github.aptd.simulation.elements.graph.network.IPlatform;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.ITrain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


/**
 * full station with different tracks and building
 *
 * @todo add action
 */
public final class CStation extends IBaseStation
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 3792779551221886558L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "station";
    /**
     * overview which platform is occupied by which train (every platform is always present als key, with null as value if unoccupied)
     */
    private final Map<IPlatform<?>, ITrain<?>> m_platformoccupation;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id station identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     * @param p_time time refernece
     */
    private CStation(
        final IAgentConfiguration<IStation<?>> p_configuration,
        final String p_id,
        final double p_longitude,
        final double p_latitude,
        final List<IPlatform<?>> p_platforms,
        final ITime p_time
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_longitude, p_latitude, p_time );
        m_platformoccupation = Collections.synchronizedMap( new HashMap<>( p_platforms != null ? p_platforms.size() : 0 ) );
        if ( p_platforms != null ) p_platforms.forEach( p -> m_platformoccupation.put( p, null ) );
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
        @SuppressWarnings( "unchecked" )
        protected final Pair<IStation<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CStation(
                    m_configuration,
                    p_data[0].toString(),
                    (double) p_data[1],
                    (double) p_data[2],
                    p_data.length > 3 ? (List<IPlatform<?>>) p_data[3] : null,
                    m_time ),
                Stream.of( FUNCTOR, BASEFUNCTOR )
            );
        }
    }


}
