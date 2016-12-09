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

package com.github.aptd.simulation.elements.graph;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * interface of graphs
 * @tparam T node identifier type
 */
public interface IGraph<T, N extends INode<T>, E extends IEdge<T>> extends Callable<IGraph<T, N, E>>
{

    /**
     * calculate a route
     *
     * @param p_start start node identifier
     * @param p_end end node identifier
     * @return list of edges to represent the route
     */
    List<E> route( final T p_start, final T p_end );

    /**
     * returns a node from the graph
     *
     * @param p_id identifier of the node
     * @return null or node object
     */
    N node( final T p_id );

    /**
     * returns an edge
     *
     * @param p_start source node identifier
     * @param p_end target node identifier
     * @return edge or null if edge not exists
     */
    E edge( final T p_start, final T p_end );

    /**
     * returns the neighbours of a node
     *
     * @param p_id node identifier
     * @return collection of neighbour nodes
     */
    Collection<N> neighbours( final T p_id );

}
