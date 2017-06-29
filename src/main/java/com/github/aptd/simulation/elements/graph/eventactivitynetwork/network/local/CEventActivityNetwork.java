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

package com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.local;

import com.github.aptd.simulation.elements.graph.IGraph;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IEventActivityNetwork;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkEvent;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


/**
 * event-activity-network
 *
 * @todo upper-bound is not implemented
 */
public class CEventActivityNetwork implements IEventActivityNetwork<INetworkEvent, INetworkActivity>
{
    /**
     * graph data structure
     */
    private final DirectedSparseGraph<INetworkEvent, INetworkActivity> m_graph = new DirectedSparseGraph<>();
    /**
     * shortest-path algorthim
     */
    private final ShortestPath<INetworkEvent, INetworkActivity> m_shortestpath;


    /**
     * ctor
     */
    public CEventActivityNetwork()
    {
        m_shortestpath = defaultshortestpath( m_graph );
    }

    /**
     * ctor
     *
     * @param p_edges edge elements
     */
    public CEventActivityNetwork( @Nonnull final Stream<INetworkActivity> p_edges )
    {
        m_shortestpath = defaultshortestpath( m_graph );
        p_edges.forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );
    }

    /**
     * ctor
     *
     * @param p_edges edge elements
     */
    public CEventActivityNetwork( @Nonnull final Stream<INetworkActivity> p_edges, @Nonnull final ShortestPath<INetworkEvent, INetworkActivity> p_shortestpath )
    {
        m_shortestpath = p_shortestpath;
        p_edges.forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );
    }

    /**
     * generates the default shortest-path algorithm
     *
     * @param p_graph graph instance
     * @return shortest-path algorithm
     */
    private static ShortestPath<INetworkEvent, INetworkActivity> defaultshortestpath( final Graph<INetworkEvent, INetworkActivity> p_graph )
    {
        final com.google.common.base.Function<INetworkActivity, Number> l_function = ( i ) -> i.cost().get().doubleValue() + i.lowerbound().get().doubleValue();
        return new DijkstraShortestPath<>( p_graph, l_function );
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

    @Nonnull
    @Override
    public List<INetworkActivity> route( @Nonnull final INetworkEvent p_start, @Nonnull final INetworkEvent p_end )
    {
        return null;
    }

    @Nonnull
    @Override
    public INetworkActivity edge( @Nonnull final INetworkEvent p_start, @Nonnull final INetworkEvent p_end )
    {
        return null;
    }

    @Nonnull
    @Override
    public Stream<INetworkEvent> neighbours( @Nonnull final INetworkEvent p_id )
    {
        return null;
    }

    @Override
    public boolean containsvertex( @Nonnull final INetworkEvent p_id )
    {
        return false;
    }

    @Override
    public boolean containsedge( @Nonnull final INetworkEvent p_start, @Nonnull final INetworkEvent p_end
    )
    {
        return false;
    }

    @Override
    public boolean containsedge( @Nonnull final INetworkActivity p_id )
    {
        return false;
    }

    @Nonnull
    @Override
    public final IGraph<INetworkEvent, INetworkActivity> addedge( @Nonnull final INetworkActivity... p_edges )
    {
        Arrays.stream( p_edges ).forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );
        return this;
    }

}
