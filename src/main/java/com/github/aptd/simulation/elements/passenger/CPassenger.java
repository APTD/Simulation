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

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.messaging.local.CMessage;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.IDoor;
import com.github.aptd.simulation.elements.train.ITrain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
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
     * qualitative state of the passenger
     */
    private EPassengerState m_state = EPassengerState.MOVING_THROUGH_STATION;
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
     * the passenger's individual speed at the station (moving to another platform) (meters per second)
     */
    private double m_speedatstation = 1.5;
    /**
     * the distance from the previous to the next platform (meters)
     */
    private double m_distancetonextplatform = 150.0;
    /**
     * distance already walked to the next platform (meters) (continuous state variable for MOVING_THROUGH_STATION)
     */
    private double m_distancewalked;
    /**
     * how long the passenger needs to enter a train when it's their turn (seconds)
     */
    private double m_entranceduration = 5.0;
    /**
     * how long the passenger needs to leave a train when it's their turn (seconds)
     */
    private double m_exitduration = 5.0;
    /**
     * how long the passenger is already using the door (seconds) (continuous state variable for ENTERING_TRAIN and LEAVING_TRAIN)
     */
    private double m_dooruse;
    /**
     * the door currently being used
     */
    private IDoor<?> m_door;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id passenger identifier
     * @param p_time time reference
     */
    private CPassenger( final IAgentConfiguration<IPassenger<?>> p_configuration, final String p_id, final ITime p_time,
                        final Stream<CItineraryEntry> p_itinerary, final double p_speedatstation )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_itinerary = new ArrayList<>( p_itinerary.collect( Collectors.toList() ) );
        m_speedatstation = p_speedatstation;
        m_nextstatechange = determinenextstatechange();
        m_nextactivation = m_nextstatechange;
        Logger.debug( "passenger " + m_id + " with speed " + m_speedatstation );
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
        switch ( m_state )
        {
            case MOVING_THROUGH_STATION:
                return m_lastcontinuousupdate.plus(
                    Math.round( ( m_distancetonextplatform - m_distancewalked ) / m_speedatstation ),
                    ChronoUnit.SECONDS
                );
            case ENTERING_TRAIN:
                return m_lastcontinuousupdate.plus(
                    Math.round( m_entranceduration - m_dooruse ),
                    ChronoUnit.SECONDS
                );
            case LEAVING_TRAIN:
                return m_lastcontinuousupdate.plus(
                    Math.round( m_exitduration - m_dooruse ),
                    ChronoUnit.SECONDS
                );
            default:
                return Instant.MAX;
        }
    }

    @Override
    protected boolean updatestate()
    {
        // if ( m_input.size() > 1 ) throw new RuntimeException( m_id + " received multiple input messages at once, not possible" );
        // this may have to be refined if further state logic is added

        if ( handleplatformmessages() || handledoormessage() ) return true;

        final List<IMessage> l_trainarriving = m_input.get( EMessageType.TRAIN_TO_PASSENGER_ARRIVING );
        if ( !l_trainarriving.isEmpty() )
        {
            if ( m_state != EPassengerState.ON_TRAIN ) throw new RuntimeException( m_id + " received ARRIVING although in state " + m_state );
            if ( !m_itinerary.get( m_itindex ).m_arrivalstation.equals( (String) l_trainarriving.get( 0 ).content()[0] ) ) return false;
            final IDoor<?> l_door = (IDoor<?>) ( (Object[]) l_trainarriving.get( 0 ).content()[2] )[0];
            output( new CMessage( this, l_door.id(), EMessageType.PASSENGER_TO_DOOR_ENQUEUE_EXIT ) );
            m_state = EPassengerState.IN_EXIT_QUEUE;
            return true;
        }

        final boolean l_timedchange = !m_nextstatechange.isAfter( m_time.current() );
        if ( l_timedchange )
        {
            switch ( m_state )
            {
                case LEAVING_TRAIN:
                    output( new CMessage( this, m_door.id(), EMessageType.PASSENGER_TO_DOOR_FINISHED ) );
                    m_door = null;
                    if ( m_itindex + 1 >= m_itinerary.size() )
                    {
                        System.out.println( m_id + " finished itinerary :-) at " + m_time.current() );
                        m_state = EPassengerState.IDLE_AT_STATION;
                    }
                    else
                    {
                        m_itindex++;
                        m_distancewalked = 0.0;
                        m_state = EPassengerState.MOVING_THROUGH_STATION;
                    }
                    break;
                case MOVING_THROUGH_STATION:
                    output( new CMessage( this, m_itinerary.get( m_itindex ).m_departuretrack, EMessageType.PASSENGER_TO_PLATFORM_SUBSCRIBE ) );
                    m_state = EPassengerState.ON_PLATFORM_WAITING_FOR_TRAIN;
                    break;
                case ENTERING_TRAIN:
                    output( new CMessage( this, m_door.id(), EMessageType.PASSENGER_TO_DOOR_FINISHED ) );
                    m_door = null;
                    output( new CMessage( this, m_itinerary.get( m_itindex ).m_trainnumber, EMessageType.PASSENGER_TO_TRAIN_SUBSCRIBE ) );
                    m_state = EPassengerState.ON_TRAIN;
                    break;
                default:
                    throw new RuntimeException( m_id + " has timed change although in state " + m_state );
            }
            return true;
        }

        return false;
    }

    private boolean handledoormessage()
    {
        final List<IMessage> l_reject = m_input.get( EMessageType.DOOR_TO_PASSENGER_REJECT );
        final List<IMessage> l_yourturn = m_input.get( EMessageType.DOOR_TO_PASSENGER_YOURTURN );
        if ( !l_yourturn.isEmpty() && !l_reject.isEmpty() )
            throw new RuntimeException( m_id + " received both REJECT and YOURTURN in state " + m_state );
        if ( !l_reject.isEmpty() )
        {
            m_state = EPassengerState.ON_PLATFORM_WAITING_FOR_TRAIN;
        }
        if ( !l_yourturn.isEmpty() )
        {
            m_door = (IDoor<?>) l_yourturn.get( 0 ).sender();
            if ( m_state == EPassengerState.IN_ENTRANCE_QUEUE )
            {
                output( new CMessage( this, m_itinerary.get( m_itindex ).m_departuretrack, EMessageType.PASSENGER_TO_PLATFORM_UNSUBSCRIBE ) );
                m_state = EPassengerState.ENTERING_TRAIN;
            }
            else if ( m_state == EPassengerState.IN_EXIT_QUEUE )
            {
                output( new CMessage( this, m_itinerary.get( m_itindex ).m_trainnumber, EMessageType.PASSENGER_TO_TRAIN_UNSUBSCRIBE ) );
                m_state = EPassengerState.LEAVING_TRAIN;
            }
            else throw new RuntimeException( m_id + " received YOURTURN from " + l_yourturn.get( 0 ).sender() + " although in state " + m_state );
            // this may have to be changed when the passenger can decide to leave a queue
            m_dooruse = 0.0;
            return true;
        }
        return false;
    }

    private boolean handleplatformmessages()
    {
        final List<IMessage> l_trainarrivedatplatform = m_input.get( EMessageType.PLATFORM_TO_PASSENGER_TRAINARRIVED );
        final List<IMessage> l_traindepartedfromplatform = m_input.get( EMessageType.PLATFORM_TO_PASSENGER_TRAINDEPARTED );

        if ( !l_trainarrivedatplatform.isEmpty() )
        {
            if ( m_state != EPassengerState.ON_PLATFORM_WAITING_FOR_TRAIN )
                throw new RuntimeException( m_id + " received TRAINARRIVED although in state" + m_state );
            if ( !m_itinerary.get( m_itindex ).m_trainnumber.equals( l_trainarrivedatplatform.get( 0 ).content()[0] ) ) return false;
            //for ( final Object l_obj : l_trainarrivedatplatform.get( 0 ).content() )
            //    System.out.println( l_obj.getClass() );
            final IDoor<?> l_door = (IDoor<?>) ( (Object[]) l_trainarrivedatplatform.get( 0 ).content()[1] )[0];
            output( new CMessage( this, l_door.id(), EMessageType.PASSENGER_TO_DOOR_ENQUEUE_ENTRANCE ) );
            m_state = EPassengerState.IN_ENTRANCE_QUEUE;
            return true;
        }

        if ( !l_traindepartedfromplatform.isEmpty() )
        {
            if ( m_state == EPassengerState.IN_ENTRANCE_QUEUE )
            {
                m_state = EPassengerState.ON_PLATFORM_WAITING_FOR_TRAIN;
                return true;
            }
            else if ( m_state == EPassengerState.ON_PLATFORM_WAITING_FOR_TRAIN ) return false;
            else throw new RuntimeException( m_id + " received TRAINDEPARTED although in state " + m_state );
        }

        return false;
    }

    @Override
    protected boolean updatecontinuous( final Duration p_elapsed )
    {
        switch ( m_state )
        {

            case MOVING_THROUGH_STATION:
                m_distancewalked += p_elapsed.get( ChronoUnit.SECONDS ) * m_speedatstation;
                return true;
            case ENTERING_TRAIN:
            case LEAVING_TRAIN:
                m_dooruse += p_elapsed.get( ChronoUnit.SECONDS );
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void writeState( final JsonGenerator p_generator ) throws IOException
    {
        p_generator.writeStringField( "state", m_state.name() );
        p_generator.writeStringField( "station", m_station == null ? null : m_station.id() );
        p_generator.writeStringField( "train", m_train == null ? null : m_train.id() );
        p_generator.writeStringField( "door", m_door == null ? null : m_door.id() );
        p_generator.writeNumberField( "dooruse", m_dooruse );
        p_generator.writeNumberField( "distancewalked", m_distancewalked );
        p_generator.writeNumberField( "itindex", m_itindex );
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
        @SuppressWarnings( "unchecked" )
        protected final Pair<IPassenger<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CPassenger( m_configuration, (String) p_data[0], m_time, (Stream<CItineraryEntry>) p_data[1], (double) p_data[2] ),
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
