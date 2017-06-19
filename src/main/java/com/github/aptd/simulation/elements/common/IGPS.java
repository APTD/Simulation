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

package com.github.aptd.simulation.elements.common;

import cern.colt.matrix.DoubleMatrix1D;
import com.github.aptd.simulation.elements.IPerceiveable;


/**
 * GPS matrix wrapper interface
 */
public interface IGPS extends IPerceiveable
{
    /**
     * returns longitude
     *
     * @return value
     */
    double longitude();

    /**
     * returns latitude
     *
     * @return value
     */
    double latitude();


    /**
     * matrix representation of the GPS object
     *
     * @return vector with 2 dimensions (longitude / latitude)
     */
    DoubleMatrix1D matrix();

}
