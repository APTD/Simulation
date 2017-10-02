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

package com.github.aptd.simulation.core.time;

import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IPerceiveable;

import java.time.Instant;
import java.util.concurrent.Callable;


/**
 * time interface
 */
public interface ITime extends IPerceiveable, Callable<ITime>
{

    /**
     * get the current time
     *
     * @return current time
     */
    Instant current();

    /**
     * cast to time instance
     *
     * @tparam N time type
     * @return casted time object
     */
    <N extends ITime> N raw();

    /**
     * add an agent
     *
     * @param p_element the new agent
     */
    void addagent( IElement<?> p_element );

    /**
     * remove an agent
     *
     * @param p_element the removed agent
     */
    void removeagent( IElement<?> p_element );

    /**
     * indicates whether the experiment is not over yet
     *
     * @return true if the experiment is not over yet, false if it is
     */
    boolean active();

}
