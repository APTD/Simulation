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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * event-activity-network
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
     *
     * @param p_edges edge elements
     */
    public CEventActivityNetwork( @Nonnull final Stream<INetworkActivity> p_edges )
    {
        p_edges.forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );

        final com.google.common.base.Function<INetworkActivity, Number> l_function = ( i ) -> i.cost().apply( i.from(), i.to() );
        m_shortestpath = new DijkstraShortestPath<>( m_graph, l_function );
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
    public List<INetworkActivity> route( final INetworkEvent p_start, final INetworkEvent p_end )
    {
        return null;
    }

    @Override
    public INetworkActivity edge( final INetworkEvent p_start, final INetworkEvent p_end )
    {
        return null;
    }

    @Override
    public Stream<INetworkEvent> neighbours( final INetworkEvent p_id )
    {
        return null;
    }

    @Override
    public boolean containsvertex( final INetworkEvent p_id )
    {
        return false;
    }

    @Override
    public boolean containsedge( final INetworkEvent p_start, final INetworkEvent p_end
    )
    {
        return false;
    }

    @Override
    public boolean containsedge( final INetworkActivity p_id )
    {
        return false;
    }

}
