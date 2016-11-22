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

package com.github.aptd.simulation.simulation.graph.network;

import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;


/**
 * abstract class of a network node
 *
 * @tparam T node type
 */
public abstract class IBaseNetworkNode<T> extends IBaseAgent<INetworkNode<T>> implements INetworkNode<T>
{
    /**
     * node identifier
     */
    protected final T m_id;
    /**
     * longitude
     */
    protected final double m_longitude;
    /**
     * latitude
     */
    protected final double m_latitude;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id node identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    public IBaseNetworkNode( final IAgentConfiguration<INetworkNode<T>> p_configuration, final T p_id, final double p_longitude, final double p_latitude )
    {
        super( p_configuration );
        m_latitude = p_latitude;
        m_longitude = p_longitude;
        m_id = p_id;
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
        return m_id.equals( p_object );
    }

}
