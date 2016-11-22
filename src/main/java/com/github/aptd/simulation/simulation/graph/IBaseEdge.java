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

import java.text.MessageFormat;


/**
 * edge class
 *
 * @tparam T node identifier type
 */
public abstract class IBaseEdge<T> implements IEdge<T>
{
    /**
     * identifier (source of the edge)
     */
    private final T m_sourceidentifier;
    /**
     * identifier (target of the edge)
     */
    private final T m_targetidentifier;
    /**
     * weight of the edge
     */
    private double m_weight;

    /**
     * ctor
     *
     * @param p_sourceidentifier source identifier of the edge
     * @param p_targetidentifier target identifiers of the edge
     */
    protected IBaseEdge( final T p_sourceidentifier, final T p_targetidentifier )
    {
        m_sourceidentifier = p_sourceidentifier;
        m_targetidentifier = p_targetidentifier;
    }

    @Override
    public final T from()
    {
        return m_sourceidentifier;
    }

    @Override
    public final double weight()
    {
        return m_weight;
    }

    @Override
    public final IEdge<T> weight( final double p_weight )
    {
        m_weight = p_weight;
        return this;
    }

    @Override
    public final T to()
    {
        return m_targetidentifier;
    }

    @Override
    public String toString()
    {
        return MessageFormat.format( "{0} --{1}--> {2}", m_sourceidentifier, m_weight, m_targetidentifier );
    }

    @Override
    public final int hashCode()
    {
        return 911 * m_sourceidentifier.hashCode() + 313 * m_targetidentifier.hashCode();
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IEdge<?> ) && ( p_object.hashCode() == this.hashCode() );
    }
}
