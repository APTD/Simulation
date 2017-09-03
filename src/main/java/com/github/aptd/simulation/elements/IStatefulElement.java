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

package com.github.aptd.simulation.elements;

import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.time.ITime;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


/**
 * stateful base element with internal transitions and output queueing
 */
@IAgentAction
public abstract class IStatefulElement<N extends IElement<?>> extends IBaseElement<N>
{

    /**
     * serial id
     */
    private static final long serialVersionUID = 446803774238459492L;

    protected Instant m_laststatechange;
    protected Instant m_lastcontinuousupdate;
    protected Instant m_nextstatechange;

    private final Set<IMessage> m_output = Collections.synchronizedSet( new HashSet<>() );

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_functor ...
     * @param p_id ...
     * @param p_time time reference
     */
    protected IStatefulElement( final IAgentConfiguration<N> p_configuration, final String p_functor, final String p_id,
                                final ITime p_time
    )
    {
        super( p_configuration, p_functor, p_id, p_time );
        m_laststatechange = m_time.current();
        m_lastcontinuousupdate = m_time.current();
    }

    protected final void flushOutput()
    {
        m_output.clear();
    }

    protected final void output( final IMessage p_message )
    {
        m_output.add( p_message );
    }

    @Override
    public synchronized Stream<IMessage> output()
    {
        final Set<IMessage> l_output = new HashSet<>( m_output );
        m_output.clear();
        return l_output.stream();
    }

    @IAgentActionFilter
    @IAgentActionName( name = "state/nextstatechange" )
    public final Instant nextstatechange()
    {
        return m_nextstatechange;
    }

    @IAgentActionFilter
    @IAgentActionName( name = "state/transition" )
    protected final synchronized void transition()
    {
        // System.out.println( m_time.current() + " - " + m_id + " transition check" );
        try
        {
            if ( updatestate() )
            {
                m_laststatechange = m_time.current();
                m_lastcontinuousupdate = m_time.current();
                m_nextstatechange = determinenextstatechange();
                System.out.println( m_time.current() + " - " + m_id + " - transition happened. next state change at " + m_nextstatechange );
            }
        }
        catch ( final RuntimeException l_ex )
        {
            System.out.println( "RUNTIME EXCEPTION in " + m_id + ": " + l_ex.toString() );
            l_ex.printStackTrace();
        }
        m_input.clear();
        m_nextactivation = m_output.isEmpty() ? m_nextstatechange : m_time.current();
        // System.out.println( m_time.current() + " - " + m_id + " output contains " + m_output.size() + " messages" );
        // m_output.forEach( msg -> System.out.println( msg.type() + " to " + msg.recipient() + ": " + msg.content() ) );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "state/continuousupdate" )
    protected final synchronized void continuousupdate()
    {
        while ( !m_nextstatechange.isAfter( m_time.current() ) ) transition();
        final Duration l_elapsed = Duration.between( m_lastcontinuousupdate, m_time.current() );
        if ( !l_elapsed.isZero() && updatecontinuous( l_elapsed ) )
        {
            m_lastcontinuousupdate = m_time.current();
            m_nextstatechange = determinenextstatechange();
            m_nextactivation = m_output.isEmpty() ? m_nextstatechange : m_time.current();
        }
    }


    /**
     * calculates the time instant at which the next (internal) state change happens unless the agent is interrupted by input.
     * pending output is ignored.
     *
     * @return time instant of next planned internal state change (apart from pending output)
     */
    protected abstract Instant determinenextstatechange();


    /**
     * this is the ONLY place where the discrete state shall be touched. continuous state can be adjusted here accordingly, too
     *
     * @return true iff the state has actually changed
     */
    protected abstract boolean updatestate();

    /**
     * apart from updatestate(), this is the only place where continuous state shall be changed; discrete state must not be changed here
     *
     * @param p_elapsed elapsed time since last continuous update
     * @return true iff the state has actually changed
     */
    protected abstract boolean updatecontinuous( final Duration p_elapsed );

    /**
     * generates a default asl script string calling "state/transition" upon "!activate", and greeting upon initialisation
     *
     * @param p_name the name to print with "hello" after initialisation
     * @return asl script as String
     */
    public static final String getDefaultAsl( final String p_name )
    {
        return "!main.\n"
               + "\n"
               + "+!main <-\n"
               + "  generic/print(\"hello " + p_name.replaceAll( "\\W", "_" ) + "\").\n"
               + "+!activate <-\n"
               + "  state/transition\n.";
    }

}
