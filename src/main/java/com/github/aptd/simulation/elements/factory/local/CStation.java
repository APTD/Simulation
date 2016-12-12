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

package com.github.aptd.simulation.elements.factory.local;

import com.github.aptd.simulation.scenario.model.graph.network.IBaseNetworkNode;
import com.github.aptd.simulation.scenario.model.graph.network.INetworkNode;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;


/**
 * full station with different tracks and building
 */
public final class CStation<T> extends IBaseNetworkNode<T>
{

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id node identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    public CStation( final IAgentConfiguration<INetworkNode<T>> p_configuration, final T p_id, final double p_longitude, final double p_latitude )
    {
        super( p_configuration, p_id, p_longitude, p_latitude );
    }

}
