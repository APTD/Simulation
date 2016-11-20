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

package com.github.aptd.simulation.simulation;

import cern.colt.matrix.DoubleMatrix1D;

import java.util.List;
import java.util.stream.Stream;


/**
 * interface to represent an environment as
 * a grid world definition
 */
public interface IEnvironment
{

    // --- dynamic object access -------------------------------------------------------------------------------------------------------------------------------

    /**
     * calculate route
     *
     * @param p_start start position
     * @param p_end target position
     * @return list of tuples of the cellindex
     */
    List<DoubleMatrix1D> route( final DoubleMatrix1D p_start, final DoubleMatrix1D p_end );

    /**
     * calculate estimated time of a route
     *
     * @param p_route current route
     * @param p_speed current speed
     * @return estimated time
     */
    double routestimatedtime( final Stream<DoubleMatrix1D> p_route, final double p_speed );

    /**
     * sets an object to the position and changes the object position
     *
     * @param p_object object, which should be moved (must store the current position)
     * @param p_position new position
     * @return updated object or object which uses the cell
     */
    IElement move( final IElement p_object, final DoubleMatrix1D p_position );

    /**
     * returns an object from the given position
     *
     * @param p_position position vector
     * @return object or null
     */
    IElement get( final DoubleMatrix1D p_position );

    /**
     * removes an element from a position
     *
     * @param p_object element
     * @return element
     */
    IElement remove( final IElement p_object );

    /**
     * checks if a position is empty
     *
     * @param p_position position
     * @return boolean result
     */
    boolean empty( final DoubleMatrix1D p_position );

    /**
     * checks if a position is inside the environment
     *
     * @param p_position position
     * @return boolean result
     */
    boolean isinside( final DoubleMatrix1D p_position );

}
