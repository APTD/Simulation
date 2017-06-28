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
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IEventActivityNetwork;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.INode;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.linearprogram.ILinearProgram;
import com.github.aptd.simulation.elements.train.ITrain;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.List;
import java.util.stream.Stream;


/**
 * event-activity-network
 *
 * @bug incomplete
 */
public class CEventActivityNetwork implements IEventActivityNetwork<ITrain, IStation<?>, EEvent>
{
    /**
     * graph data structure
     */
    private final DirectedSparseGraph<INode, IActivity> m_graph = new DirectedSparseGraph<>();
    /**
     * shortest-path algorthm
     */
    private final DijkstraShortestPath<INode, IActivity> m_dijekstra;



    /**
     * ctor
     *
     * @param p_edges edge elements
     */
    public CEventActivityNetwork( final Stream<IActivity> p_edges )
    {
        p_edges.forEach( i -> m_graph.addEdge( i, i.from(), i.to() ) );
        m_dijekstra = new DijkstraShortestPath<>( m_graph, null );
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
    public final List<IActivity> route( final INode p_start, final INode p_end )
    {
        return m_dijekstra.getPath( p_start, p_end );
    }

    @Override
    public final IActivity edge( final INode p_start, final INode p_end )
    {
        return m_graph.findEdge( p_start, p_end );
    }

    @Override
    public final Stream<INode> neighbours( final INode p_id )
    {
        return m_graph.containsVertex( p_id )
               ? m_graph.getNeighbors( p_id ).stream()
               : Stream.of();
    }

    @Override
    public final boolean containsvertex( final INode p_id )
    {
        return m_graph.containsVertex( p_id );
    }

    @Override
    public final boolean containsedge( final INode p_start, final INode p_end )
    {
        return m_graph.containsEdge( m_graph.findEdge( p_start, p_end ) );
    }

    @Override
    public final boolean containsedge( final IActivity p_id )
    {
        return m_graph.containsEdge( p_id );
    }


    @Override
    public List<EEvent> route( final IActivity<ITrain, IStation<?>, EEvent> p_start, final IActivity<ITrain, IStation<?>, EEvent> p_end )
    {
        return null;
    }

    @Override
    public EEvent edge( final IActivity<ITrain, IStation<?>, EEvent> p_start, final IActivity<ITrain, IStation<?>, EEvent> p_end
    )
    {
        return null;
    }

    @Override
    public Stream<IActivity<ITrain, IStation<?>, EEvent>> neighbours( final IActivity<ITrain, IStation<?>, EEvent> p_id
    )
    {
        return null;
    }

    @Override
    public boolean containsvertex( final IActivity<ITrain, IStation<?>, EEvent> p_id
    )
    {
        return false;
    }

    @Override
    public boolean containsedge( final IActivity<ITrain, IStation<?>, EEvent> p_start, final IActivity<ITrain, IStation<?>, EEvent> p_end
    )
    {
        return false;
    }

    @Override
    public boolean containsedge( final EEvent p_id )
    {
        return false;
    }
}
