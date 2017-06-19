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

package com.github.aptd.simulation.elements.passenger;

import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.IElement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


/**
 * passenger
 */
public final class CPassenger extends IBaseElement<IPassenger<?>> implements IPassenger<IPassenger<?>>
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -2850497411803927233L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "passenger";

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id passenger identifier
     * @param p_time time reference
     */
    private CPassenger( final IAgentConfiguration<IPassenger<?>> p_configuration, final String p_id, final ITime p_time )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
    }

    @Override
    protected final Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return Stream.of();
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IPassenger<?>>
    {
        /**
         * object counter
         */
        private static final AtomicLong COUNTER = new AtomicLong();

        /**
         * @param p_stream stream
         * @param p_actions action
         * @param p_time time reference
         * @throws Exception on any error
         */
        protected CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final ITime p_time ) throws Exception
        {
            super( p_stream, p_actions, CPassenger.class, p_time );
        }

        @Override
        protected final Pair<IPassenger<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CPassenger( m_configuration, MessageFormat.format( "{0} {1}", FUNCTOR.toLowerCase(), COUNTER.getAndIncrement() ), m_time ),
                Stream.of( FUNCTOR )
            );
        }

        @Override
        public final IGenerator<IPassenger<?>> resetcount()
        {
            COUNTER.set( 0 );
            return this;
        }
    }

}
