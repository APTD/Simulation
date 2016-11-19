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

package com.github.aptd.simulation.graph.local;

import com.github.aptd.simulation.graph.INode;
import com.github.aptd.simulation.graph.network.IVirtualNode;
import com.github.aptd.simulation.object.train.ITrain;

import java.text.MessageFormat;


/**
 * virtual node
 */
public final class CVirtualNode<T> implements IVirtualNode<T>
{
    /**
     * node identifier
     */
    private final T m_id;
    /**
     * longitude
     */
    private final double m_longitude;
    /**
     * latitude
     */
    private final double m_latitude;


    /**
     * ctor
     * @param p_id node identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    public CVirtualNode( final T p_id, final double p_longitude, final double p_latitude )
    {
        m_latitude = p_latitude;
        m_longitude = p_longitude;
        m_id = p_id;
    }


    @Override
    public final T id()
    {
        return m_id;
    }

    @Override
    public final double longitude()
    {
        return m_longitude;
    }

    @Override
    public final double latitude()
    {
        return m_latitude;
    }

    @Override
    public final int hashCode()
    {
        return m_id.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof INode<?> ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}({1} / {2})", m_id, m_longitude, m_latitude );
    }

    @Override
    public final ITrain apply( final ITrain p_train )
    {
        return p_train;
    }
}
