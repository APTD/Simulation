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

package com.github.aptd.simulation.graph;

import com.github.aptd.simulation.graph.local.CLocalFactory;
import com.github.aptd.simulation.graph.network.INetworkNode;


/**
 * interface of a graph factory
 */
public interface IFactory
{

    /**
     * generates a graph instance
     *
     * @tparam T node identifier type
     * @tparam N node type
     * @tparam E edge type
     * @return graph instance
     */
    <T, N extends INode<T>, E extends IEdge<T>> IGraph<T, N, E> graph();

    /**
     * generates virtual network node
     *
     * @param p_id node identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    <T> INetworkNode<T> networkvirtualnode( final T p_id, final double p_longitude, final double p_latitude );





    /**
     * factory
     */
    enum EFactory
    {
        LOCAL( new CLocalFactory() );

        /**
         * factory instance
         */
        private final IFactory m_factory;

        /**
         * ctor
         *
         * @param p_factory factory object
         */
        EFactory( final IFactory p_factory )
        {
            m_factory = p_factory;
        }

        /**
         * returns the factory
         *
         * @return factory
         */
        IFactory factory()
        {
            return m_factory;
        }


    }
}
