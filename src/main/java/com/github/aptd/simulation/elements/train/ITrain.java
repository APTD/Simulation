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

package com.github.aptd.simulation.elements.train;



import com.github.aptd.simulation.elements.IElement;


/**
 * interface of train
 */
public interface ITrain<T extends ITrain<?>> extends IElement<T>
{

    /**
     * wagon number
     *
     * @return wagon number
     */
    int wagon();

    /**
     * adds a new wagon
     *
     * @param p_wagon wagon
     * @return self reference
     */
    ITrain<T> addwagon( final IWagon<?> p_wagon );

    /**
     * removes the last wagon
     *
     * @return last wagon
     */
    IWagon<?> removewagon();


}
