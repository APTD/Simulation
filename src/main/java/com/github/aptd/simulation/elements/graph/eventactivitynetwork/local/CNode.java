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

package com.github.aptd.simulation.elements.graph.eventactivitynetwork.local;

import com.github.aptd.simulation.elements.graph.eventactivitynetwork.INode;
import com.github.aptd.simulation.elements.graph.network.IStation;


/**
 * node
 */
public class CNode implements INode
{
    private final IStation<?> m_station;

    /**
     * ctor
     *
     * @param p_station station
     */
    public CNode( final IStation<?> p_station )
    {
        m_station = p_station;
    }

    @Override
    public final IStation<?> station()
    {
        return m_station;
    }

    @Override
    public final int hashCode()
    {
        return m_station.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof INode ) && ( p_object.hashCode() == this.hashCode() );
    }
}
