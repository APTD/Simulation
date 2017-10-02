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

import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.messaging.local.CMessage;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.common.IGPS;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.github.aptd.simulation.error.CSemanticException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * train class
 */
@IAgentAction
public final class CTrain extends IStatefulElement<ITrain<?>> implements ITrain<ITrain<?>>
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 7268435045100070725L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "train";
    /**
     * average driving speed (not considering acceleration and braking at the moment)
     * @todo make a non-static field for instances
     */
    private static final double DRIVING_SPEED = 40.0;
    /**
     * list of wagons
     * @todo change to map with wagon names and ordering
     */
    private final List<IWagon<?>> m_wagon;
    /**
     * lists of doors
     */
    private final Set<IDoor<?>> m_doorsclosedlocked = Collections.synchronizedSet( new HashSet<>() );
    private final Set<IDoor<?>> m_doorsnotclosedlocked = Collections.synchronizedSet( new HashSet<>() );
    /**
     * set of passengers on the train (subscribers to notifcations of arrivals at stations)
     */
    private final Set<IPassenger<?>> m_passengers = Collections.synchronizedSet( new HashSet<>() );
    /**
     * current GPS position
     */
    private final IGPS m_position = null;

    private ETrainState m_state = ETrainState.ARRIVED;

    private final List<CTimetableEntry> m_timetable;
    private int m_ttindex;
    private double m_positionontrack;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id train identifier
     * @param p_timetable stream of timetable entries
     * @param p_doors list of door agent references
     * @param p_wagon stream of wagon references
     * @param p_time environment
     */
    private CTrain( final IAgentConfiguration<ITrain<?>> p_configuration, final String p_id, final Stream<CTimetableEntry> p_timetable,
                    final List<IDoor<?>> p_doors, final Stream<IWagon<?>> p_wagon, final ITime p_time )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_wagon = p_wagon.collect( Collectors.toList() );
        m_doorsnotclosedlocked.addAll( p_doors );
        m_timetable = p_timetable.collect( Collectors.toList() );
        output( new CMessage( this, m_timetable.get( m_ttindex ).m_platformid, EMessageType.TRAIN_TO_PLATFORM_ARRIVING,
                              m_doorsnotclosedlocked.toArray() ) );
        // first timetable entry only has departure
        m_nextstatechange = determinenextstatechange();
        m_nextactivation = m_time.current();
    }

    @Override
    public final int wagon()
    {
        return m_wagon.size();
    }

    @Override
    public final ITrain<ITrain<?>> addwagon( final IWagon<?> p_wagon )
    {
        m_wagon.add( p_wagon );
        return this;
    }

    @Override
    public final IWagon<?> removewagon()
    {
        if ( m_wagon.size() == 1 )
            throw new CSemanticException( "train [{0}] cannot be empty", m_id );

        return m_wagon.remove( m_wagon.size() - 1 );
    }


    @Override
    protected Stream<ILiteral> individualliteral( final Stream<com.github.aptd.simulation.elements.IElement<?>> p_object )
    {
        return Stream.of(
            CLiteral.from( "wagon", CRawTerm.from( this.wagon() ) )
        );
    }

    @Override
    protected final synchronized Instant determinenextstatechange()
    {
        switch ( m_state )
        {
            case DRIVING:
                return m_lastcontinuousupdate.plus(
                    Math.round( ( m_timetable.get( m_ttindex ).m_tracklength - m_positionontrack ) / DRIVING_SPEED ),
                    ChronoUnit.SECONDS
                );
            case WAITING_TO_DRIVE:
                return m_doorsnotclosedlocked.isEmpty() ? m_laststatechange : Instant.MAX;
            case ARRIVED:
                if ( m_ttindex + 1 >= m_timetable.size() ) return Instant.MAX;
                return Collections.max( Arrays.asList( m_timetable.get( m_ttindex ).m_publisheddeparture, m_laststatechange.plus( 30, ChronoUnit.SECONDS ) ) );
            default:
                return m_laststatechange.plus( 30, ChronoUnit.SECONDS );
        }
    }

    @Override
    protected final synchronized boolean updatestate()
    {
        final List<IDoor<?>> l_doorsclosedlocked = m_input.get( EMessageType.DOOR_TO_TRAIN_CLOSED_LOCKED ).stream()
                                                          .map( msg -> (IDoor<?>) msg.sender() ).collect( Collectors.toList() );
        if ( !l_doorsclosedlocked.isEmpty() )
        {
            if ( m_state != ETrainState.WAITING_TO_DRIVE ) throw new RuntimeException( "door locked although not waiting to drive: " + m_id );
            m_doorsnotclosedlocked.removeAll( l_doorsclosedlocked );
            m_doorsclosedlocked.addAll( l_doorsclosedlocked );
        }

        final List<IMessage> l_subscribingpassengers = m_input.get( EMessageType.PASSENGER_TO_TRAIN_SUBSCRIBE );
        final List<IMessage> l_unsubscribingpassengers = m_input.get( EMessageType.PASSENGER_TO_TRAIN_UNSUBSCRIBE );
        if ( !l_subscribingpassengers.isEmpty() || !l_unsubscribingpassengers.isEmpty() )
        {
            if ( m_doorsnotclosedlocked.isEmpty() ) throw new RuntimeException( "passengers subscribing/unsubscribing although all doors locked:" + m_id );
            l_subscribingpassengers.stream().forEach( msg -> m_passengers.add( (IPassenger<?>) msg.sender() ) );
            l_unsubscribingpassengers.stream().forEach( msg -> m_passengers.remove( msg.sender() ) );
        }

        final boolean l_timedchange = !m_nextstatechange.isAfter( m_time.current() );
        // if ( l_timedchange )
        //    System.out.println( m_id + " - timer transition at " + m_time.current().toString() + " from state " + m_state + " (ttindex = " + m_ttindex + ")" );
        switch ( m_state )
        {
            case ARRIVED:
                if ( !l_timedchange ) break;
                System.out.println( m_id + " - departure at " + m_time.current().toString() + " which was planned for "
                                    + m_timetable.get( m_ttindex ).m_publisheddeparture );
                // proceed to next timetable entry
                m_ttindex++;
                m_positionontrack = 0.0;
                m_state = ETrainState.WAITING_TO_DRIVE;
                m_doorsnotclosedlocked.forEach( d -> output( new CMessage( this, d.id(), EMessageType.TRAIN_TO_DOOR_LOCK, "" ) ) );
                // debugPrintState();
                return true;
            case DRIVING:
                // @todo: react to "red signal" here
                if ( !l_timedchange ) break;
                m_state = ETrainState.ARRIVED;
                System.out.println( m_id + " - arrival at " + m_time.current().toString() + " which was planned for "
                                    + m_timetable.get( m_ttindex ).m_publishedarrival );
                m_doorsclosedlocked.forEach( d -> output( new CMessage( this, d.id(), EMessageType.TRAIN_TO_DOOR_UNLOCK,
                                                                        m_timetable.get( m_ttindex ).m_stationid,
                                                                        m_timetable.get( m_ttindex ).m_platformid ) ) );
                m_doorsnotclosedlocked.addAll( m_doorsclosedlocked );
                m_doorsclosedlocked.clear();
                m_passengers.forEach( p -> output( new CMessage( this,
                                                                 p.id(),
                                                                 EMessageType.TRAIN_TO_PASSENGER_ARRIVING,
                                                                 m_timetable.get( m_ttindex ).m_stationid,
                                                                 m_timetable.get( m_ttindex ).m_platformid,
                                                                 m_doorsnotclosedlocked.toArray()
                ) ) );
                output( new CMessage( this, m_timetable.get( m_ttindex ).m_platformid, EMessageType.TRAIN_TO_PLATFORM_ARRIVING,
                                      m_doorsnotclosedlocked.toArray() ) );
                // debugPrintState();
                return true;
            case WAITING_TO_DRIVE:
                if ( !m_doorsnotclosedlocked.isEmpty() ) break;
                output( new CMessage( this, m_timetable.get( m_ttindex - 1 ).m_platformid, EMessageType.TRAIN_TO_PLATFORM_DEPARTING ) );
                m_state = ETrainState.DRIVING;
                debugPrintState();
                return true;
            default:
                // making checkstyle happy
        }
        return false;
    }

    @Override
    protected final synchronized boolean updatecontinuous( final Duration p_elapsed )
    {
        switch ( m_state )
        {
            case DRIVING:
                m_positionontrack += p_elapsed.get( ChronoUnit.SECONDS ) * DRIVING_SPEED;
                return true;
            default:
                // making checkstyle happy
        }
        return false;
    }

    private void debugPrintState()
    {
        System.out.println( m_id + " - state is " + m_state + ", ttindex is " + m_ttindex );
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<ITrain<?>>
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
            super( p_stream, p_actions, CTrain.class, p_time );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected final Pair<ITrain<?>, Stream<String>> generate( final Object... p_data )
        {
            // ( (Stream<CTimetableEntry>) p_data[1] ).forEach( e -> System.out.println( e ) );
            return new ImmutablePair<>(
                new CTrain(
                    m_configuration,
                    p_data[0].toString(),
                    (Stream<CTimetableEntry>) p_data[1],
                    (List<IDoor<?>>) p_data[2],
                    Arrays.stream( p_data ).skip( 3 ).map( i -> (IWagon<?>) i ),
                    m_time
                ),
                Stream.of( FUNCTOR )
            );
        }
    }

    /**
     * an entry in the timetable of the train
     */
    public static final class CTimetableEntry
    {

        private final double m_tracklength;
        private final String m_stationid;
        private final String m_platformid;
        private final Instant m_publishedarrival;
        private final Instant m_publisheddeparture;

        /**
         * ctor
         *
         * @param p_tracklength length of the track leading to the station
         * @param p_stationid station
         * @param p_platformid platform
         * @param p_publishedarrival arrival at this station
         * @param p_publisheddeparture departure onto track of next entry
         */
        public CTimetableEntry( final double p_tracklength, final String p_stationid, final String p_platformid, final Instant p_publishedarrival,
                                final Instant p_publisheddeparture )
        {
            m_tracklength = p_tracklength;
            m_stationid = p_stationid;
            m_platformid = p_platformid;
            m_publishedarrival = p_publishedarrival;
            m_publisheddeparture = p_publisheddeparture;
        }

        public String toString()
        {
            return "TTE - " + m_tracklength + " / " + m_stationid + " / " + m_platformid + " / " + m_publishedarrival + " / " + m_publisheddeparture;
        }

    }

}
