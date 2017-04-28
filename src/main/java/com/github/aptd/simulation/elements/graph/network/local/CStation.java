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

import cern.colt.matrix.DoubleMatrix1D;
import com.github.aptd.simulation.common.CGPS;
import com.github.aptd.simulation.common.IGPS;
import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * full station with different tracks and building
 *
 * @todo add action
 */
public final class CStation extends IBaseElement<IStation<?>> implements IStation<IStation<?>>
{
    /**
     * literal functor
     */
    private static final String FUNCTOR = "station";
    /**
     * GPS position latitude / longitude
     */
    private final IGPS m_position;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id station identifier
     */
    private CStation( final IAgentConfiguration<IStation<?>> p_configuration, final String p_id, final double p_longitude, final double p_latitude )
    {
        super( p_configuration, FUNCTOR, p_id );
        m_position = new CGPS( p_longitude, p_latitude );

    }

    @Override
    public final DoubleMatrix1D gps()
    {
        return m_position.matrix();
    }

    @Override
    protected final Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return m_position.literal( p_object );
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
         * @param p_aggregation aggregation
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final IAggregation p_aggregation ) throws Exception
        {
            super( p_stream, p_actions, p_aggregation, CStation.class );
        }

        @Override
        protected final Pair<IStation<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CStation( m_configuration, p_data[0].toString(), (double) p_data[1], (double) p_data[1] ),
                Stream.of( FUNCTOR )
            );
        }
    }


}