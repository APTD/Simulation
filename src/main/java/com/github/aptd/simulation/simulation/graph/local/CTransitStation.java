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

import com.github.aptd.simulation.simulation.graph.network.IBaseNetworkNode;
import com.github.aptd.simulation.simulation.graph.network.INetworkNode;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;


/**
 * transit station class to define a station without details of tracks
 *
 * @tparam T identifier type
 */
public final class CTransitStation<T> extends IBaseNetworkNode<T>
{

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id node identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    public CTransitStation( final IAgentConfiguration<INetworkNode<T>> p_configuration, final T p_id, final double p_longitude, final double p_latitude )
    {
        super( p_configuration, p_id, p_longitude, p_latitude );
    }

}
