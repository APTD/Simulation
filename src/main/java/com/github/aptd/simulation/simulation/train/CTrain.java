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

package com.github.aptd.simulation.simulation.train;

import com.github.aptd.simulation.common.CAgentTrigger;
import com.github.aptd.simulation.simulation.error.CSemanticException;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * base train class
 */
@IAgentAction
public final class CTrain<T> extends IBaseAgent<ITrain<T>> implements ITrain<T>
{
    /**
     * train identifier
     */
    private final T m_id;
    /**
     * list of wagons
     */
    private final List<IWagon> m_wagon;


    /**
     * ctor
     *
     * @param p_agent agent configuration
     * @param p_id train identifier
     * @param p_wagon wagon definition
     */
    protected CTrain( final IAgentConfiguration<ITrain<T>> p_agent, final T p_id, final IWagon... p_wagon )
    {
        this( p_agent, p_id, Arrays.stream( p_wagon ) );
    }


    /**
     * ctor
     *
     * @param p_agent agent configuration
     * @param p_id train identifier
     * @param p_wagon wagon definition
     */
    protected CTrain( final IAgentConfiguration<ITrain<T>> p_agent, final T p_id, final Stream<IWagon> p_wagon )
    {
        super( p_agent );
        m_id = p_id;
        m_wagon = p_wagon.filter( Objects::nonNull ).collect( Collectors.toCollection( CopyOnWriteArrayList::new ) );
    }

    @Override
    public final T id()
    {
        return m_id;
    }

    @Override
    public final int wagon()
    {
        return m_wagon.size();
    }

    @Override
    public final int hashCode()
    {
        return m_id.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof ITrain<?> ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final ITrain addwagon( final IWagon p_wagon )
    {
        if ( p_wagon != null )
            m_wagon.add( p_wagon );
        return this;
    }

    @Override
    public final IWagon removewagon()
    {
        if ( m_wagon.size() == 1 )
            throw new CSemanticException( "train [{0}] cannot be empty", m_id );

        return m_wagon.remove( m_wagon.size() - 1 );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "train/announcement" )
    private void announcement( final Object p_any )
    {
        final ITrigger l_trigger = CAgentTrigger.wagonannouncement( p_any );
        m_wagon.parallelStream().forEach( i -> i.announcement( l_trigger ) );
    }

}
