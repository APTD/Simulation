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

import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.messaging.local.CMessage;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.graph.network.IPlatform;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.github.aptd.simulation.elements.train.IDoor;
import com.github.aptd.simulation.elements.train.ITrain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


/**
 * platform class
 */
public final class CPlatform extends IStatefulElement<IPlatform<?>> implements IPlatform<IPlatform<?>>
{

    /**
     * serial id
     */
    private static final long serialVersionUID = -7374587357204465585L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "platform";
    /**
     * ID of the station this platform belongs to
     */
    private final String m_stationid;
    /**
     * set of passengers at the platform (subscribers to notifcations of arriving trains)
     */
    private final Set<IPassenger<?>> m_passengers = Collections.synchronizedSet( new HashSet<>() );
    /**
     * reference to the train currently standing at the platform
     */
    private ITrain<?> m_train;
    private final List<IDoor<?>> m_doors = Collections.synchronizedList( new LinkedList<>() );

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id ...
     * @param p_stationid ID of the station
     * @param p_time time reference
     */
    private CPlatform(
        final IAgentConfiguration<IPlatform<?>> p_configuration,
        final String p_id,
        final String p_stationid,
        final ITime p_time
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_stationid = p_stationid;
    }

    @Override
    protected Instant determinenextstatechange()
    {
        // a platform has no processes going on
        return Instant.MAX;
    }

    @Override
    protected boolean updatestate()
    {
        if ( m_input.isEmpty() ) return false;

        final List<IMessage> l_arrivingtrains = m_input.get( EMessageType.TRAIN_TO_PLATFORM_ARRIVING );
        final List<IMessage> l_departingtrains = m_input.get( EMessageType.TRAIN_TO_PLATFORM_DEPARTING );
        final List<IMessage> l_subscribingpassengers = m_input.get( EMessageType.PASSENGER_TO_PLATFORM_SUBSCRIBE );
        final List<IMessage> l_unsubscribingpassengers = m_input.get( EMessageType.PASSENGER_TO_PLATFORM_UNSUBSCRIBE );

        if ( l_departingtrains.size() > 1 ) throw new RuntimeException( m_id + " has multiple trains departing simultaneously at " + m_time.current() );
        if ( l_departingtrains.size() == 1 )
        {
            if ( m_train != l_departingtrains.get( 0 ).sender() )
                throw new RuntimeException( m_id + " has a train departing that's not there at " + m_time.current() );
            m_passengers.stream().forEach( p -> output( new CMessage( this, p.id(), EMessageType.PLATFORM_TO_PASSENGER_TRAINDEPARTED, m_train.id() ) ) );
            m_train = null;
            m_doors.clear();
        }

        l_subscribingpassengers.stream().forEach( msg ->
        {
            m_passengers.add( (IPassenger) msg.sender() );
            if ( m_train != null ) output( new CMessage( this, msg.sender().id(), EMessageType.PLATFORM_TO_PASSENGER_TRAINARRIVED, m_train.id(),
                                                         m_doors.toArray() ) );
        } );
        l_unsubscribingpassengers.stream().forEach( msg -> m_passengers.remove( msg.sender() ) );

        if ( l_arrivingtrains.size() > 1 ) throw new RuntimeException( m_id + " has multiple trains arriving simultaneously at " + m_time.current() );
        if ( l_arrivingtrains.size() == 1 )
        {
            if ( m_train != null )
                throw new RuntimeException( m_id + " has a second train arriving without the first departing at " + m_time.current() );
            m_train = (ITrain) l_arrivingtrains.get( 0 ).sender();
            Arrays.stream( l_arrivingtrains.get( 0 ).content() ).map( o -> (IDoor<?>) o ).sorted( Comparator.comparing( IDoor::id ) )
                  .forEachOrdered( m_doors::add );
            m_passengers.stream().forEach( p -> output( new CMessage( this, p.id(), EMessageType.PLATFORM_TO_PASSENGER_TRAINARRIVED, m_train.id(),
                                                                      m_doors.toArray() ) ) );
        }
        return true;
    }

    @Override
    protected boolean updatecontinuous( final Duration p_elapsed )
    {
        // a platform has no continuous state
        return false;
    }

    @Override
    protected Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return Stream.of();
    }

    @Override
    public String stationid()
    {
        return m_stationid;
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IPlatform<?>>
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
            super( p_stream, p_actions, CPlatform.class, p_time );
        }

        @Override
        protected Pair<IPlatform<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CPlatform(
                    m_configuration,
                    p_data[0].toString(),
                    p_data[1].toString(),
                    m_time
                ),
                Stream.of( FUNCTOR )
            );
        }
    }

}
