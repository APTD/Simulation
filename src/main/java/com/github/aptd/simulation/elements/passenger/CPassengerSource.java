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
import org.apache.commons.math3.distribution.RealDistribution;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


/**
 * passenger source: an agent creating Passenger agents according to statistical distributions
 */
@IAgentAction
public final class CPassengerSource extends IBaseElement<IPassengerSource<?>>
        implements IPassengerSource<IPassengerSource<?>>
{

    /**
     * serial id
     */
    private static final long serialVersionUID = 5248638076491134344L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "passengersource";

    private RealDistribution m_distribution;
    private long m_startmillis;
    private int m_passengers;
    private int m_passengersgenerated;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id ...
     * @param p_time time reference
     */
    private CPassengerSource(
        final IAgentConfiguration<IPassengerSource<?>> p_configuration,
        final String p_id,
        final ITime p_time,
        final RealDistribution p_distribution,
        final long p_startmillis,
        final int p_passengers
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_distribution = p_distribution;
        m_startmillis = p_startmillis;
        m_passengers = p_passengers;
        m_passengersgenerated = 0;
        m_nextactivation = nextstatechange();
    }

    /**
     * define object literal addons
     *
     * @param p_object calling objects
     * @return literal stream
     */
    @Override
    protected Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object )
    {
        return Stream.of();
    }

    private synchronized Instant nextstatechange()
    {
        if ( m_passengersgenerated >= m_passengers ) return Instant.MAX;
        return Instant.ofEpochMilli( m_startmillis + (long) Math.ceil( m_distribution.inverseCumulativeProbability(
                1.0 * ( m_passengersgenerated + 1 ) / m_passengers ) ) );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "state/timertransition" )
    private synchronized void timertransition()
    {
        if ( nextstatechange().isAfter( m_time.current() ) ) return;
        System.out.println( "generating passenger at " + m_time.current().toString() );
        generatepassenger();
        m_nextactivation = nextstatechange();
    }

    private synchronized void generatepassenger()
    {
        // @todo implement
        m_passengersgenerated++;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IPassengerSource<?>>
    {
        /**
         * object counter
         */
        private static final AtomicLong COUNTER = new AtomicLong();

        /**
         * generator ctor
         * @param p_stream stream
         * @param p_actions action
         * @param p_time time reference
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final ITime p_time )
                throws Exception
        {
            super( p_stream, p_actions, CPassengerSource.class, p_time );
        }

        @Override
        protected final Pair<IPassengerSource<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                    new CPassengerSource( m_configuration,
                            MessageFormat.format( "{0} {1}", FUNCTOR.toLowerCase(), COUNTER.getAndIncrement() ),
                            m_time,
                            (RealDistribution) p_data[0], (long) p_data[1], (int) p_data[2] ),
                    Stream.of( FUNCTOR )
            );
        }

        @Override
        public final IGenerator<IPassengerSource<?>> resetcount()
        {
            COUNTER.set( 0 );
            return this;
        }
    }


}
