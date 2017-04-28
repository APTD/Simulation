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

package com.github.aptd.simulation.elements.graph.network.local;

import com.github.aptd.simulation.elements.graph.IGraph;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;


/**
 * graph class with local data
 *
 * @tparam T node identifier type
 * @see http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/graph/package-summary.html
 */
public class CNetwork implements IGraph<IStation<?>, ITrack<?>>
{
    /**
     * graph data structure
     */
    private final DirectedSparseGraph<IStation<?>, ITrack<?>> m_graph = new DirectedSparseGraph<>();
    /**
     * shortest-path algorthm
     */
    private final DijkstraShortestPath<IStation<?>, ITrack<?>> m_dijekstra;



    /**
     * ctor
     *
     * @param p_edges edge elements
     */
    public CNetwork( final Collection<ITrack<?>> p_edges )
    {
        this( p_edges, null );
    }

    /**
     * ctor
     *
     * @param p_edges edge elements
     * @param p_weightfunction weight function
     */
    public CNetwork( final Collection<ITrack<?>> p_edges, final Function<ITrack<?>, ? extends Number> p_weightfunction )
    {
        p_edges.forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );
        m_dijekstra = new DijkstraShortestPath<>( m_graph, p_weightfunction );
    }


    @Override
    public final int hashCode()
    {
        return m_graph.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IGraph<?, ?> ) && ( p_object.hashCode() == this.hashCode() );
    }

    @Override
    public final String toString()
    {
        return m_graph.toString();
    }

    @Override
    public final List<ITrack<?>> route( final IStation<?> p_start, final IStation<?> p_end )
    {
        return m_dijekstra.getPath( p_start, p_end );
    }

    @Override
    public final ITrack edge( final IStation<?> p_start, final IStation<?> p_end )
    {
        return m_graph.findEdge( p_start, p_end );
    }

    @Override
    public final Stream<IStation<?>> neighbours( final IStation<?> p_id )
    {
        return m_graph.containsVertex( p_id )
               ? m_graph.getNeighbors( p_id ).stream()
               : Stream.of();
    }

    @Override
    public final boolean containsvertex( final IStation<?> p_id )
    {
        return m_graph.containsVertex( p_id );
    }

    @Override
    public final boolean containsedge( final IStation<?> p_start, final IStation<?> p_end )
    {
        return m_graph.containsEdge( m_graph.findEdge( p_start, p_end ) );
    }

    @Override
    public final boolean containsedge( final ITrack<?> p_id )
    {
        return m_graph.containsEdge( p_id );
    }

}
