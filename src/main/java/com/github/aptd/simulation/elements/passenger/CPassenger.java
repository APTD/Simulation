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
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.ITrain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


/**
 * passenger
 */
@IAgentAction
public final class CPassenger extends IStatefulElement<IPassenger<?>> implements IPassenger<IPassenger<?>>
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
     * station that the passenger is currently at (may be null, especially if they are on a train instead)
     */
    private IStation<?> m_station;
    /**
     * train that the passenger is currently on (may be null, especially if they are at a station instead)
     */
    private ITrain<?> m_train;

    /**
     * the passenger's origin (station) from where they begin their journey
     */
    private String m_origin;
    /**
     * the passenger's destination (station)
     */
    private String m_destination;
    /**
     * how the passenger currently intends to get from their origin to their destination
     */
    private List<CItineraryEntry> m_itinerary;
    /**
     * index into the itinerary
     */
    private int m_itindex;

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

    @Override
    protected Instant determinenextstatechange()
    {
        // @todo implement
        return Instant.MAX;
    }

    @Override
    protected boolean updatestate()
    {
        // @todo implement
        return false;
    }

    @Override
    protected boolean updatecontinuous( final Duration p_elapsed )
    {
        // @todo implement
        return false;
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
         * generator ctor
         * @param p_stream stream
         * @param p_actions action
         * @param p_time time reference
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final ITime p_time ) throws Exception
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

    /**
     * a part of a passenger's itinerary
     * train, track and station information is represented via Strings rather than object references for two reasons:
     * 1. the itinerary represents the knowlegde (beliefs) of the passenger, which may be simulated as wrong or fuzzy
     * 2. the corresponding simulation objects (especially trains) may not have been instantiated at the time of itinerary planning
     */
    public static final class CItineraryEntry
    {

        /**
         * the number of the train the passenger intends to use (as known to them)
         */
        private final String m_trainnumber;
        /**
         * the name of the station at which the passenger intends to board the train (as known to them)
         */
        private final String m_departurestation;
        /**
         * the name of the station at which the passenger intends to alight from the train (as known to them)
         */
        private final String m_arrivalstation;
        /**
         * the time at which the passenger thinks the train will depart from the station where they want to board
         */
        private final Instant m_departuretime;
        /**
         * the time at which the passenger thinks the train will arrive at the station where they want to alight
         */
        private final Instant m_arrivaltime;
        /**
         * the name/number of the track inside the departurestation from which the passenger thinks the train will depart
         */
        private final String m_departuretrack;
        /**
         * the name/number of the track inside the arrivalstation at which the passenger thinks the train will arrive
         */
        private final String m_arrivaltrack;

        /**
         * ctor
         *
         * @param p_trainnumber the number of the train the passenger intends to use (as known to them)
         * @param p_departurestation the name of the station at which the passenger intends to board the train (as known to them)
         * @param p_arrivalstation the name of the station at which the passenger intends to alight from the train (as known to them)
         * @param p_departuretime the time at which the passenger thinks the train will depart from the station where they want to board
         * @param p_arrivaltime the time at which the passenger thinks the train will arrive at the station where they want to alight
         * @param p_departuretrack the name/number of the track inside the departurestation from which the passenger thinks the train will depart
         * @param p_arrivaltrack the name/number of the track inside the arrivalstation at which the passenger thinks the train will arrive
         */
        public CItineraryEntry( final String p_trainnumber, final String p_departurestation, final String p_arrivalstation,
                                final Instant p_departuretime,
                                final Instant p_arrivaltime,
                                final String p_departuretrack,
                                final String p_arrivaltrack
        )
        {
            m_trainnumber = p_trainnumber;
            m_departurestation = p_departurestation;
            m_arrivalstation = p_arrivalstation;
            m_departuretime = p_departuretime;
            m_arrivaltime = p_arrivaltime;
            m_departuretrack = p_departuretrack;
            m_arrivaltrack = p_arrivaltrack;
        }
    }

}
