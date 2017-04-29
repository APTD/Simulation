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

package com.github.aptd.simulation.elements.graph.network;

import cern.colt.matrix.DoubleMatrix1D;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.IVertex;


/**
 * interface of a network node
 *
 * @tparam T node identifier
 */
public interface INode<T extends INode<?>> extends IVertex, IElement<T>
{

    /**
     * gps position as vector
     *
     * @return 2-dimensional vector (latitude / longitude)
     */
    DoubleMatrix1D gps();

}