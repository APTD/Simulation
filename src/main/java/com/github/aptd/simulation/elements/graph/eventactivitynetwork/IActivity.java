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

package com.github.aptd.simulation.elements.graph.eventactivitynetwork;

import com.github.aptd.simulation.elements.graph.IEdge;

import javax.annotation.Nonnull;
import java.util.function.Supplier;


/**
 * event interface
 */
public interface IActivity<S,T,E> extends IEdge<IEvent<S,T,E>>
{

    /**
     * cost function
     *
     * @return supplier cost
     */
    @Nonnull
    Supplier<Number> cost();

    /**
     * lower-bound
     *
     * @return lower-bound supplier
     */
    @Nonnull
    Supplier<Number> lowerbound();

    /**
     * upper-bound
     *
     * @return upper-bound supplier
     */
    @Nonnull
    Supplier<Number> upperbound();

    /**
     * event
     *
     * @return event reference
     */
    @Nonnull
    E event();

}
