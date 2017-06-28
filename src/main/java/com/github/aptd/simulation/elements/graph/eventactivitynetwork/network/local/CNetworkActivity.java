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


import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.ITrain;

import javax.annotation.Nonnull;
import java.time.Instant;


/**
 * activity
 */
public final class CNetworkActivity implements INetworkActivity
{
    /**
     * train
     */
    private final ITrain<?> m_train;
    /**
     * station
     */
    private final IStation<?> m_station;
    /**
     * event
     */
    private final EActivity m_event;
    /**
     * time
     */
    private final Instant m_time;

    /**
     * ctor
     *
     * @param p_train train
     * @param p_station station
     * @param p_event event
     * @param p_time time
     */
    public CNetworkActivity( @Nonnull final ITrain<?> p_train, @Nonnull final IStation<?> p_station,
                             @Nonnull final EActivity p_event, @Nonnull final Instant p_time )
    {
        m_time = p_time;
        m_train = p_train;
        m_event = p_event;
        m_station = p_station;
    }



    @Nonnull
    @Override
    public final ITrain<?> source()
    {
        return m_train;
    }

    @Nonnull
    @Override
    public final IStation<?> target()
    {
        return m_station;
    }

    @Nonnull
    @Override
    public final EActivity event()
    {
        return m_event;
    }


    @Override
    public final int hashCode()
    {
        return m_event.hashCode() ^ m_train.hashCode() ^ m_station.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IActivity<?, ?, ?> ) && ( p_object.hashCode() == this.hashCode() );
    }

    @Nonnull
    @Override
    public final Instant time()
    {
        return m_time;
    }
}
