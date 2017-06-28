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

import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.common.IGPS;
import com.github.aptd.simulation.error.CSemanticException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * train class
 */
@IAgentAction
public final class CTrain extends IBaseElement<ITrain<?>> implements ITrain<ITrain<?>>
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
     * current GPS position
     */
    private final IGPS m_position = null;

    private ETrainState m_state = ETrainState.ARRIVED;

    private Instant m_laststatechange;
    private Instant m_lastupdate;

    private final List<CTimetableEntry> m_timetable;
    private int m_ttindex;
    private double m_positionontrack;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id train identifier
     * @param p_wagon wagon references
     * @param p_time environment
     */
    private CTrain( final IAgentConfiguration<ITrain<?>> p_configuration, final String p_id, final Stream<CTimetableEntry> p_timetable,
                    final Stream<IWagon<?>> p_wagon, final ITime p_time )
    {
        super( p_configuration, p_id, FUNCTOR, p_time );
        m_wagon = p_wagon.collect( Collectors.toList() );
        m_timetable = p_timetable.collect( Collectors.toList() );
        // first timetable entry only has departure
        m_laststatechange = m_time.current();
        m_nextactivation = nextstatechange();
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

    @IAgentActionFilter
    @IAgentActionName( name = "state/nextstatechange" )
    private final synchronized Instant nextstatechange()
    {
        switch ( m_state )
        {
            case DRIVING:
                return m_lastupdate.plus(
                    Math.round( ( m_timetable.get( m_ttindex ).m_tracklength - m_positionontrack ) / DRIVING_SPEED ),
                    ChronoUnit.SECONDS
                );
            case WAITING_TO_DRIVE:
                return m_laststatechange;
            case ARRIVED:
                if ( m_ttindex + 1 >= m_timetable.size() ) return Instant.MAX;
                return Collections.max( Arrays.asList( m_timetable.get( m_ttindex ).m_publisheddeparture, m_laststatechange.plus( 30, ChronoUnit.SECONDS ) ) );
            default:
                return m_laststatechange.plus( 30, ChronoUnit.SECONDS );
        }
    }

    @IAgentActionFilter
    @IAgentActionName( name = "state/timertransition" )
    private final synchronized void timertransition()
    {
        if ( nextstatechange().isAfter( m_time.current() ) ) return;
        System.out.println( "timer transition at " + m_time.current().toString() + " from state " + m_state + " (ttindex = " + m_ttindex + ")" );
        switch ( m_state )
        {
            case ARRIVED:
                // proceed to next timetable entry
                m_ttindex++;
                m_positionontrack = 0.0;
                m_state = ETrainState.WAITING_TO_DRIVE;
                break;
            case DRIVING:
                m_state = ETrainState.ARRIVED;
                break;
            case WAITING_TO_DRIVE:
                m_state = ETrainState.DRIVING;
                break;
            default:
                // making checkstyle happy
        }
        System.out.println( "new state is " + m_state + ", ttindex is " + m_ttindex );
        m_laststatechange = m_time.current();
        m_lastupdate = m_time.current();
        m_nextactivation = nextstatechange();
    }

    private final synchronized void update()
    {
        if ( !nextstatechange().isBefore( m_time.current() ) ) timertransition();
        final Duration l_elapsed = Duration.between( m_lastupdate, m_time.current() );
        switch ( m_state )
        {
            case DRIVING:
                m_positionontrack += l_elapsed.get( ChronoUnit.SECONDS ) * DRIVING_SPEED;
                break;
            default:
                // making checkstyle happy
        }
        m_lastupdate = m_time.current();
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
                    Arrays.stream( p_data ).skip( 2 ).map( i -> (IWagon<?>) i ),
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
        private final Instant m_publishedarrival;
        private final Instant m_publisheddeparture;

        /**
         * ctor
         *
         * @param p_tracklength length of the track leading to the station
         * @param p_stationid station
         * @param p_publishedarrival arrival at this station
         * @param p_publisheddeparture departure onto track of next entry
         */
        public CTimetableEntry( final double p_tracklength, final String p_stationid, final Instant p_publishedarrival, final Instant p_publisheddeparture )
        {
            m_tracklength = p_tracklength;
            m_stationid = p_stationid;
            m_publishedarrival = p_publishedarrival;
            m_publisheddeparture = p_publisheddeparture;
        }

        public String toString()
        {
            return "TTE - " + m_tracklength + " / " + m_stationid + " / " + m_publishedarrival + " / " + m_publisheddeparture;
        }

    }

}
