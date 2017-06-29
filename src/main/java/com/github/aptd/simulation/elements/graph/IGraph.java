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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * interface of graphs
 * @tparam V node type
 * @tparam E edge type
 */
public interface IGraph<V extends IVertex<?>, E extends IEdge<V>>
{

    /**
     * calculate a route
     *
     * @param p_start start node identifier
     * @param p_end end node identifier
     * @return list of edges to represent the route
     */
    @Nonnull
    List<E> route( @Nonnull final V p_start, @Nonnull final V p_end );

    /**
     * returns an edge
     *
     * @param p_start source node identifier
     * @param p_end target node identifier
     * @return edge or null if edge not exists
     */
    @Nonnull
    E edge( @Nonnull final V p_start, @Nonnull final V p_end );

    /**
     * returns the neighbours of a node
     *
     * @param p_id node identifier
     * @return stream of neighbour nodes
     */
    @Nonnull
    Stream<V> neighbours( @Nonnull final V p_id );

    /**
     * checks if a vertex exist
     *
     * @param p_id vertex
     * @return existing flag
     */
    boolean containsvertex( @Nonnull final V p_id );

    /**
     * checks if an edge exists
     *
     * @param p_start start vertex
     * @param p_end end vertex
     * @return existing flag
     */
    boolean containsedge( @Nonnull final V p_start, @Nonnull final V p_end );

    /***
     * checks if an edge exist
     *
     * @param p_id edge
     * @return existing flag
     */
    boolean containsedge( @Nonnull final E p_id );

    /**
     * adds edges
     *
     * @param p_edges edges
     * @return self reference
     */
    @Nonnull
    @SuppressWarnings( "unchecked" )
    IGraph<V, E> addedge( @Nonnull final E... p_edges );

}
