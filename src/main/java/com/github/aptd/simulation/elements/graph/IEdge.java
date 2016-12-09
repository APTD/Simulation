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


/**
 * interface of edges
 */
public interface IEdge<T>
{

    /**
     * returns the identifier of the source / start node
     * on which the edge starts
     *
     * @return node identifier
     */
    T from();

    /**
     * return the identifier of the target / end node
     * on which the edge points to
     *
     * @return node identifier
     */
    T to();

    /**
     * returns the weight of the edge
     *
     * @return weight
     */
    double weight();

    /**
     * sets the weight
     *
     * @param p_weight new weight of the edge
     * @return self reference
     */
    IEdge<T> weight( final double p_weight );

}
