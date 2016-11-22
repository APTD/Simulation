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

package com.github.aptd.simulation.simulation.train;

import com.github.aptd.simulation.passenger.IPassenger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import java.util.stream.Stream;


/**
 * wagon interface
 */
public interface IWagon
{

    /**
     * anouncement for all agents within the wagon
     *
     * @param p_trigger announcement trigger
     * @return self reference
     */
    IWagon announcement( final ITrigger p_trigger );

    /**
     * unused places
     *
     * @return places
     */
    int free();

    /**
     * number of free placeses
     *
     * @return size
     */
    int size();

    /**
     * adds a passenger to the wagon
     *
     * @param p_passenger passanger agent
     * @return self reference
     */
    IWagon add( final IPassenger<?> p_passenger );

    /**
     * removes a passenger from the wagon
     * @param p_passenger passenger
     * @return self reference
     */
    IWagon remove( final IPassenger<?> p_passenger );

    /**
     * stream over all passenger
     *
     * @return passenger stream
     */
    Stream<IPassenger<?>> stream();

}
