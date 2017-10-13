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

package com.github.aptd.simulation.elements.train;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.messaging.local.CMessage;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * door class
 */
public final class CDoor extends IStatefulElement<IDoor<?>> implements IDoor<IDoor<?>>
{

    /**
     * serial id
     */
    private static final long serialVersionUID = -5363342346164082638L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "door";

    private Queue<IPassenger<?>> m_exitqueue = new LinkedList<>();
    private Queue<IPassenger<?>> m_entryqueue = new LinkedList<>();

    /**
     * the train agent this door belongs to
     */
    private String m_train;

    private EDoorState m_state = EDoorState.CLOSED_RELEASED;
    /**
     * the total width of the door (when fully open) (meters)
     */
    private double m_width;
    /**
     * how far the door is actually open at the moment (min 0.0, max m_width), in between in OPENING and CLOSING states (meters)
     */
    private double m_openwidth;
    /**
     * for how long the door must have been unused before it can close (seconds)
     */
    private double m_minfreetimetoclose = 10;
    /**
     * for how long the door has not been used (seconds)
     */
    private double m_freetime;
    /**
     * how fast the door opens (meters per second)
     */
    private double m_openingspeed = 0.4;
    /**
     * how fast the door closes (meters per second)
     */
    private double m_closingspeed = 0.4;
    /**
     * agent ID of the station the door is currently at
     */
    private String m_stationid;
    /**
     * agent ID of the platform the door is currently at
     */
    private String m_platformid;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id            ...
     * @param p_time          time reference
     * @param p_train train id
     */
    protected CDoor( final IAgentConfiguration<IDoor<?>> p_configuration, final String p_id, final ITime p_time, final String p_train, final double p_width )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_train = p_train;
        m_width = p_width;
        m_nextstatechange = determinenextstatechange();
    }

    @Override
    protected Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object )
    {
        return Stream.of();
    }

    @Override
    protected Instant determinenextstatechange()
    {
        switch ( m_state )
        {
            case CLOSED_LOCKED:
            case CLOSED_RELEASED:
            case OPEN_BUSY:
            case OPEN_BUSY_SHALL_CLOSE:
            case OPEN_CLOSEABLE:
                return Instant.MAX;
            case OPENING:
            case OPENING_SHALL_CLOSE:
                return m_lastcontinuousupdate.plus(
                    Math.round( ( m_width - m_openwidth ) / m_openingspeed ),
                    ChronoUnit.SECONDS
                );
            case OPEN_FREE:
            case OPEN_FREE_SHALL_CLOSE:
                return m_lastcontinuousupdate.plus(
                    Math.round( m_minfreetimetoclose - m_freetime ),
                    ChronoUnit.SECONDS
                );
            case CLOSING:
            case CLOSING_LOCKED:
                return m_lastcontinuousupdate.plus(
                    Math.round( m_openwidth / m_closingspeed ),
                    ChronoUnit.SECONDS
                );
            default:
                return Instant.MAX;
        }
    }

    @Override
    protected boolean updatestate()
    {
        //debugPrintState();
        final EDoorState l_oldstate = m_state;
        final List<IMessage> l_entryrequests = m_input.get( EMessageType.PASSENGER_TO_DOOR_ENQUEUE_ENTRANCE );
        final List<IMessage> l_exitrequests = m_input.get( EMessageType.PASSENGER_TO_DOOR_ENQUEUE_EXIT );
        final List<IMessage> l_finished = m_input.get( EMessageType.PASSENGER_TO_DOOR_FINISHED );

        // enqueuing passengers (if queues are empty, passenger will get immediate access with nextpassengerifpossible() at the end)
        if ( m_state != EDoorState.CLOSED_LOCKED )
        {
            l_entryrequests.stream().sorted( Comparator.comparing( msg -> msg.sender().id() ) )
                           .forEachOrdered( msg -> m_entryqueue.add( (IPassenger<?>) msg.sender() ) );
            l_exitrequests.stream().sorted( Comparator.comparing( msg -> msg.sender().id() ) )
                          .forEachOrdered( msg -> m_exitqueue.add( (IPassenger<?>) msg.sender() ) );
        }
        else
        {
            l_entryrequests.stream().sorted( Comparator.comparing( msg -> msg.sender().id() ) )
                           .forEachOrdered( msg -> output( new CMessage( this, msg.sender().id(), EMessageType.DOOR_TO_PASSENGER_REJECT ) ) );
            l_exitrequests.stream().sorted( Comparator.comparing( msg -> msg.sender().id() ) )
                          .forEachOrdered( msg -> output( new CMessage( this, msg.sender().id(), EMessageType.DOOR_TO_PASSENGER_REJECT ) ) );
        }

        // locking or unlocking
        handletrainmessages();

        // switches state from busy to free, but if queues are not empty, will become busy again with nextpassengerifpossible() at the end
        if ( l_finished.size() > 1 ) throw new RuntimeException( m_id + " received multiple finished messages" );
        if ( !l_finished.isEmpty() ) passengerfinished( l_finished.get( 0 ) );

        // determine from which queue a passenger could be taken next: exit has priority over entry
        final Queue<IPassenger<?>> l_queue = m_exitqueue.isEmpty() ? m_entryqueue.isEmpty() ? null : m_entryqueue : m_exitqueue;

        // finish opening/closing process if applicable; switch to closable if door is open and has been unused for m_minfreetimetoclose seconds
        timedstatechangeifnecessary( l_queue );

        // if the door is in a permissible state, let the next passenger pass (or open the door first, if closed), if any
        nextpassengerifpossible( l_queue );

        //debugPrintState();
        return l_oldstate != m_state;
    }

    private void debugPrintState()
    {
        System.out.println( "[DEBUG] " + m_id + " state: " + m_state );
    }

    private void handletrainmessages()
    {
        final List<IMessage> l_lockrequests = m_input.get( EMessageType.TRAIN_TO_DOOR_LOCK );
        final List<IMessage> l_unlockrequests = m_input.get( EMessageType.TRAIN_TO_DOOR_UNLOCK );
        final List<IMessage> l_departing = m_input.get( EMessageType.TRAIN_TO_DOOR_DEPARTING );

        if ( !l_lockrequests.isEmpty() && !l_unlockrequests.isEmpty() )
            throw new RuntimeException( "door " + m_id + " received lock and unlock request at the same time" );

        if ( ( !l_lockrequests.isEmpty() || !l_unlockrequests.isEmpty() ) && !l_departing.isEmpty() )
            throw new RuntimeException( "door " + m_id + " received lock/unlock request and departing message at the same time" );

        lockifrequested( l_lockrequests );

        unlockifrequested( l_unlockrequests );

        if ( l_departing.size() > 1 ) throw new RuntimeException( m_id + " received multiple departing messages" );
        if ( !l_departing.isEmpty() )
        {
            if ( m_state != EDoorState.CLOSED_LOCKED ) throw new RuntimeException( m_id + " received departing message although in state " + m_state );
            m_entryqueue.forEach( p -> System.out.println( p.id() + " in entry queue when departing" ) );
            m_exitqueue.forEach( p -> System.out.println( p.id() + " in exit queue when departing" ) );
            m_entryqueue.clear();
            m_stationid = null;
            m_platformid = null;
        }
    }

    private void unlockifrequested( final List<IMessage> p_unlockrequests )
    {
        if ( p_unlockrequests.size() > 1 ) throw new RuntimeException( m_id + " received multiple unlock requests" );
        if ( !p_unlockrequests.isEmpty() )
        {
            m_stationid = (String) p_unlockrequests.get( 0 ).content()[0];
            m_platformid = (String) p_unlockrequests.get( 0 ).content()[1];
            switch ( m_state )
            {
                case CLOSED_LOCKED:
                    m_state = m_exitqueue.isEmpty() && m_entryqueue.isEmpty() ? EDoorState.CLOSED_RELEASED : EDoorState.OPENING;
                    break;
                case OPENING_SHALL_CLOSE:
                    m_state = EDoorState.OPENING;
                    break;
                case OPEN_FREE_SHALL_CLOSE:
                    m_state = EDoorState.OPEN_FREE;
                    break;
                case OPEN_BUSY_SHALL_CLOSE:
                    m_state = EDoorState.OPEN_BUSY;
                    break;
                case CLOSING_LOCKED:
                    m_state = EDoorState.CLOSING;
                    break;
                case CLOSED_RELEASED:
                case OPENING:
                case OPEN_FREE:
                case OPEN_BUSY:
                case OPEN_CLOSEABLE:
                case CLOSING:
                    System.out.println( "WARNING: door " + m_id + " received unlock request although already in state " + m_state + " at " + m_time.current() );
                    break;
                default:
                    // making checkstyle happy
            }
        }
    }

    private void lockifrequested( final List<IMessage> p_lockrequests )
    {
        if ( p_lockrequests.size() > 1 ) throw new RuntimeException( m_id + " received multiple lock requests" );
        if ( !p_lockrequests.isEmpty() )
            switch ( m_state )
            {
                case CLOSED_RELEASED:
                    m_state = EDoorState.CLOSED_LOCKED;
                    output( new CMessage( this, m_train, EMessageType.DOOR_TO_TRAIN_CLOSED_LOCKED, "" ) );
                    break;
                case OPENING:
                    m_state = EDoorState.OPENING_SHALL_CLOSE;
                    break;
                case OPEN_FREE:
                    m_state = EDoorState.OPEN_FREE_SHALL_CLOSE;
                    break;
                case OPEN_BUSY:
                    m_state = EDoorState.OPEN_BUSY_SHALL_CLOSE;
                    break;
                case OPEN_CLOSEABLE:
                    m_state = EDoorState.CLOSING_LOCKED;
                    break;
                case CLOSING:
                    m_state = EDoorState.CLOSING_LOCKED;
                    break;
                case CLOSED_LOCKED:
                case OPENING_SHALL_CLOSE:
                case OPEN_FREE_SHALL_CLOSE:
                case OPEN_BUSY_SHALL_CLOSE:
                case CLOSING_LOCKED:
                    System.out.println( "WARNING: door " + m_id + " received lock request although already in state " + m_state );
                    break;
                default:
                    // making checkstyle happy
            }
    }

    private void timedstatechangeifnecessary( final Queue<IPassenger<?>> p_queue )
    {
        final boolean l_timedchange = !m_nextstatechange.isAfter( m_time.current() );
        if ( l_timedchange )
        {
            // System.out.println( m_id + " - timer transition at " + m_time.current().toString() + " from state " + m_state );
            switch ( m_state )
            {
                case OPENING:
                case OPENING_SHALL_CLOSE:
                    m_openwidth = m_width;
                    m_state = m_state == EDoorState.OPENING ? EDoorState.OPEN_FREE : EDoorState.OPEN_FREE_SHALL_CLOSE;
                    m_freetime = 0.0;
                    break;
                case OPEN_FREE_SHALL_CLOSE:
                    if ( p_queue == null ) m_state = EDoorState.CLOSING_LOCKED;
                    // in case of a passenger enqueueing exactly when the door would start to close, the passenger wins and it does not close
                    break;
                case OPEN_FREE:
                    m_state = EDoorState.OPEN_CLOSEABLE;
                    break;
                case CLOSING:
                    m_state = EDoorState.CLOSED_RELEASED;
                    m_openwidth = 0.0;
                    break;
                case CLOSING_LOCKED:
                    m_state = EDoorState.CLOSED_LOCKED;
                    m_openwidth = 0.0;
                    output( new CMessage( this, m_train, EMessageType.DOOR_TO_TRAIN_CLOSED_LOCKED, "" ) );
                    break;
                default:
                    // making checkstyle happy
            }
        }
    }

    private void passengerfinished( final IMessage p_message )
    {
        switch ( m_state )
        {
            case OPEN_BUSY:
                m_state = EDoorState.OPEN_FREE;
                break;
            case OPEN_BUSY_SHALL_CLOSE:
                m_state = EDoorState.OPEN_FREE_SHALL_CLOSE;
                break;
            default:
                throw new RuntimeException( m_id + " received finished message although not busy in state " + m_state );
        }
        final String l_exiting = m_exitqueue.isEmpty() ? null : m_exitqueue.peek().id();
        final String l_entering = m_entryqueue.isEmpty() ? null : m_entryqueue.peek().id();
        if ( p_message.sender().id().equals( l_exiting ) ) m_exitqueue.poll();
        else if ( p_message.sender().id().equals( l_entering ) ) m_entryqueue.poll();
        else throw new RuntimeException( m_id + " received finished message from " + p_message.sender().id() + " who is not first in either queue" );
        m_freetime = 0.0;
    }

    private void nextpassengerifpossible( final Queue<IPassenger<?>> p_queue )
    {
        if ( p_queue != null )
        {
            switch ( m_state )
            {
                case OPEN_CLOSEABLE:
                case OPEN_FREE:
                    m_state = EDoorState.OPEN_BUSY;
                    output( new CMessage( this, p_queue.peek().id(), EMessageType.DOOR_TO_PASSENGER_YOURTURN, m_stationid, m_platformid ) );
                    break;
                case OPEN_FREE_SHALL_CLOSE:
                    m_state = EDoorState.OPEN_BUSY_SHALL_CLOSE;
                    output( new CMessage( this, p_queue.peek().id(), EMessageType.DOOR_TO_PASSENGER_YOURTURN, m_stationid, m_platformid ) );
                    break;
                case CLOSING:
                case CLOSED_RELEASED:
                    m_state = EDoorState.OPENING;
                    break;
                case CLOSING_LOCKED:
                    m_state = EDoorState.OPENING_SHALL_CLOSE;
                    break;
                default:
                    // making checkstyle happy
            }
        }
    }

    @Override
    protected boolean updatecontinuous( final Duration p_elapsed )
    {
        switch ( m_state )
        {
            case OPENING:
            case OPENING_SHALL_CLOSE:
                m_openwidth += p_elapsed.get( ChronoUnit.SECONDS ) * m_openingspeed;
                return true;
            case OPEN_FREE:
            case OPEN_CLOSEABLE:
            case OPEN_FREE_SHALL_CLOSE:
                m_freetime += p_elapsed.get( ChronoUnit.SECONDS );
                return true;
            case CLOSING:
            case CLOSING_LOCKED:
                m_openwidth -= p_elapsed.get( ChronoUnit.SECONDS ) * m_closingspeed;
                return true;
            default:
                // making checkstyle happy
        }
        return false;
    }

    @Override
    protected void writeState( final JsonGenerator p_generator ) throws IOException
    {
        p_generator.writeStringField( "state", m_state.name() );
        p_generator.writeNumberField( "openwidth", m_openwidth );
        p_generator.writeStringField( "station", m_stationid );
        p_generator.writeStringField( "platform", m_platformid );
        p_generator.writeNumberField( "freetime", m_freetime );
        p_generator.writeArrayFieldStart( "entryqueue" );
        for ( final IPassenger<?> l_passenger : m_entryqueue ) p_generator.writeString( l_passenger.id() );
        p_generator.writeEndArray();
        p_generator.writeArrayFieldStart( "exitqueue" );
        for ( final IPassenger<?> l_passenger : m_exitqueue ) p_generator.writeString( l_passenger.id() );
        p_generator.writeEndArray();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IDoor<?>>
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
            super( p_stream, p_actions, CDoor.class, p_time );
        }

        @Override
        protected final Pair<IDoor<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                    new CDoor( m_configuration,
                               (String) p_data[0],
                               m_time,
                               (String) p_data[1],
                               (double) p_data[2] ),
                    Stream.of( FUNCTOR )
            );
        }

        @Override
        public final IGenerator<IDoor<?>> resetcount()
        {
            COUNTER.set( 0 );
            return this;
        }
    }
}
