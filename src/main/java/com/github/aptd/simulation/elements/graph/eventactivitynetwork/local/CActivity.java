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


import com.github.aptd.simulation.elements.graph.eventactivitynetwork.EEvent;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.INode;


/**
 * activity
 */
public final class CActivity implements IActivity
{
    /**
     * event
     */
    private final EEvent m_event;
    /**
     * from node
     */
    private final INode m_from;
    /**
     * to node
     */
    private final INode m_to;

    /**
     * ctor
     *
     * @param p_event event
     * @param p_from from node
     * @param p_to to node
     */
    public CActivity( final EEvent p_event, final INode p_from, final INode p_to )
    {
        m_event = p_event;
        m_from = p_from;
        m_to = p_to;
    }


    @Override
    public final INode from()
    {
        return m_from;
    }

    @Override
    public final INode to()
    {
        return m_to;
    }

    @Override
    public final EEvent event()
    {
        return m_event;
    }

    @Override
    public final int hashCode()
    {
        return m_event.hashCode() ^ m_from.hashCode() ^ m_to.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IActivity ) && ( p_object.hashCode() == this.hashCode() );
    }
}
