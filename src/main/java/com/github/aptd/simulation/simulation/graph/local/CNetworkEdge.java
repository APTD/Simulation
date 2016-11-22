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

import com.github.aptd.simulation.simulation.graph.IBaseEdge;
import com.github.aptd.simulation.simulation.graph.network.INetworkEdge;


/**
 * network edge class to link to nodes
 */
public final class CNetworkEdge<T> extends IBaseEdge<T> implements INetworkEdge<T>
{
    /**
     * ctor
     *
     * @param p_sourceidentifier source identifier of the edge
     * @param p_targetidentifier target identifiers of the edge
     */
    protected CNetworkEdge( final T p_sourceidentifier, final T p_targetidentifier )
    {
        super( p_sourceidentifier, p_targetidentifier );
    }

    @Override
    public final double maximumspeed()
    {
        return 0;
    }

}
