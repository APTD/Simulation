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

package com.github.aptd.simulation.elements;

import com.github.aptd.simulation.elements.common.EDirection;
import org.lightjason.agentspeak.agent.IAgent;

import java.util.List;
import java.util.stream.Stream;

/**
 * environment interface to define a moveable structure of an agent
 *
 * @todo must be refactored based on the agents
 */
public interface IEnvironment<T extends IAgent<?>, N> extends IPerceiveable
{

    /**
     * calculates the route estimated time
     *
     * @param p_route route
     * @param p_speed speed
     * @return estimated time
     */
    double estimatedtime( final Stream<N> p_route, final double p_speed );

    /**
     * calculates a route
     *
     * @param p_start start position
     * @param p_end end position
     * @return list with stopovers and end position, list is defined as (start, end]
     */
    List<N> route( final N p_start, final N p_end );

    /**
     * moves an element in the environment
     *
     * @param p_direction direction
     * @param p_speed speed of the element
     * @param p_goal goal position
     * @return element which should moved or another element which blocks the position
     */
    T move( final T p_element, final EDirection p_direction, final double p_speed, final N p_goal );

}
