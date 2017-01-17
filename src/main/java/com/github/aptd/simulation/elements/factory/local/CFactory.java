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

package com.github.aptd.simulation.elements.factory.local;

import com.github.aptd.simulation.elements.graph.IEdge;
import com.github.aptd.simulation.elements.factory.IFactory;
import com.github.aptd.simulation.elements.graph.IGraph;
import com.github.aptd.simulation.elements.graph.INode;
import com.github.aptd.simulation.scenario.model.graph.network.INetworkNode;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;


/**
 * factory class of the local graph
 */
public final class CFactory implements IFactory
{
    @Override
    public final <T, N extends INode<T>, E extends IEdge<T>> IGraph<T, N, E> graph()
    {
        return null;
    }

    @Override
    public final <T> INetworkNode<T> networkvirtualnode( final IAgentConfiguration<INetworkNode<T>> p_agent, final T p_id, final double p_longitude, final double p_latitude )
    {
        return new CVirtualNetworkNode<T>( p_agent, p_id, p_longitude, p_latitude );
    }
}