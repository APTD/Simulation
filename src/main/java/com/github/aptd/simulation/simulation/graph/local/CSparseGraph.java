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

package com.github.aptd.simulation.simulation.graph.local;

import com.github.aptd.simulation.simulation.graph.IEdge;
import com.github.aptd.simulation.simulation.graph.IGraph;
import com.github.aptd.simulation.simulation.graph.INode;
import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * graph class with local data
 *
 * @tparam T node identifier type
 * @see http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/graph/package-summary.html
 */
public class CSparseGraph<T, N extends INode<T>, E extends IEdge<T>> implements IGraph<T, N, E>
{
    /**
     * graph data structure
     */
    private final DirectedSparseGraph<N, E> m_graph = new DirectedSparseGraph<>();
    /**
     * shortest-path algorthm
     */
    private final DijkstraShortestPath<N, E> m_dijekstra;
    /**
     * map with identifier and node objects
     */
    private final Map<T, N> m_nodemap;


    /**
     * ctor
     *
     * @param p_nodes nodes elements
     * @param p_edges edge elements
     */
    public CSparseGraph( final Collection<N> p_nodes, final Collection<E> p_edges )
    {
        this( p_nodes, p_edges, IEdge::weight );
    }

    /**
     * ctor
     *
     * @param p_nodes nodes elements
     * @param p_edges edge elements
     * @param p_weightfunction weight function
     */
    public CSparseGraph( final Collection<N> p_nodes, final Collection<E> p_edges, final Function<E, ? extends Number> p_weightfunction )
    {
        p_nodes.forEach( m_graph::addVertex );

        m_nodemap = Collections.unmodifiableMap(
                p_nodes.stream()
                        .collect( Collectors.toMap( INode::id, i -> i ) )
        );

        p_edges.stream()
                .filter( i -> m_nodemap.containsKey( i.from() ) && m_nodemap.containsKey( i.to() ) )
                .forEach( i -> m_graph.addEdge( i, m_nodemap.get( i.from() ), m_nodemap.get( i.to() ) ) );

        m_dijekstra = new DijkstraShortestPath<>( m_graph, p_weightfunction );
    }


    @Override
    public final List<E> route( final T p_start, final T p_end )
    {
        return m_dijekstra.getPath( this.node( p_start ), this.node( p_end ) );
    }

    @Override
    public final N node( final T p_id )
    {
        return m_nodemap.get( p_id );
    }

    @Override
    public final E edge( final T p_start, final T p_end )
    {
        return m_graph.findEdge( this.node( p_start ), this.node( p_end ) );
    }

    @Override
    public final Collection<N> neighbours( final T p_id )
    {
        final N l_node = this.node( p_id );
        return ( l_node != null ) && ( m_graph.containsVertex( l_node ) )
                ? m_graph.getNeighbors( l_node )
                : Collections.<N>emptySet();
    }

    @Override
    public final int hashCode()
    {
        return m_graph.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IGraph<?, ?, ?> ) && ( p_object.hashCode() == this.hashCode() );
    }

    @Override
    public final String toString()
    {
        return m_graph.toString();
    }

    @Override
    public final IGraph<T, N, E> call() throws Exception
    {
        m_nodemap.values()
                 .parallelStream()
                 .forEach( INode::execute );

        return this;
    }
}
