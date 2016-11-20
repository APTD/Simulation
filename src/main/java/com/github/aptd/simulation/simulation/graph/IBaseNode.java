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

package com.github.aptd.simulation.simulation.graph;

import com.github.aptd.simulation.common.CCommon;

import java.text.MessageFormat;

/**
 * define a graph node
 * @tparam T any typo of the node identifier to get a more flexible structure
 * @deprecated
 */
@Deprecated
public abstract class IBaseNode<T> implements INode<T>
{
    /**
     * unique node identifier
     */
    private final T m_id;


    /**
     * ctor
     *
     * @param p_id unique identifier
     */
    public IBaseNode( final T p_id )
    {
        if ( p_id == null )
            throw new IllegalArgumentException( CCommon.languagestring( IBaseNode.class, "nodeid" ) );
        m_id = p_id;
    }


    @Override
    public final T id()
    {
        return m_id;
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
    public String toString()
    {
        return MessageFormat.format( "(id: {0})", m_id );
    }
}
