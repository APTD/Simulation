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

package com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.local;

import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkEvent;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;


/**
 * network event
 */
public final class CNetworkActivity implements INetworkActivity
{
    /**
     * from activity
     */
    private final INetworkEvent m_from;
    /**
     * activity
     */
    private final INetworkEvent m_to;
    /**
     * event
     */
    private final EEvent m_event;

    /**
     * ctor
     *
     * @param p_from from activity
     * @param p_to to activity
     * @param p_event event
     */
    public CNetworkActivity( @Nonnull final INetworkEvent p_from, @Nonnull final INetworkEvent p_to, @Nonnull final EEvent p_event )
    {
        m_to = p_to;
        m_from = p_from;
        m_event = p_event;
    }

    @Override
    public final INetworkEvent from()
    {
        return m_from;
    }

    @Override
    public final INetworkEvent to()
    {
        return m_to;
    }

    @Override
    public final int hashCode()
    {
        return m_from.hashCode() ^ m_to.hashCode() ^ m_event.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof INetworkActivity ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Nonnull
    @Override
    public final BiFunction<INetworkEvent, INetworkEvent, Number> cost()
    {
        return ( i, j ) -> ChronoUnit.MINUTES.between( j.time(), i.time() );
    }

    @Nonnull
    @Override
    public final EEvent event()
    {
        return null;
    }
}
