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
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.ENetworkEvent;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkEvent;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.ITrain;


/**
 * activity
 */
public final class CNetworkActivity implements INetworkActivity
{
    /**
     * train
     */
    private final ITrain m_train;
    /**
     * station
     */
    private final IStation<?> m_station;
    /**
     * event
     */
    private final INetworkEvent m_event;

    /**
     * ctor
     *
     * @param p_train train
     * @param p_station station
     * @param p_event event
     */
    public CNetworkActivity( final ITrain p_train, final IStation<?> p_station, final INetworkEvent p_event )
    {
        m_train = p_train;
        m_station = p_station;
        m_event = p_event;
    }



    @Override
    public final ITrain source()
    {
        return m_train;
    }

    @Override
    public final IStation<?> target()
    {
        return m_station;
    }

    @Override
    public final INetworkEvent event()
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
        return ( p_object != null ) && ( p_object instanceof IActivity<?,?,?> ) && ( p_object.hashCode() == this.hashCode() );
    }
}
