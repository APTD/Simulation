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

package com.github.aptd.simulation.elements.passenger;

/**
 * enum of qualitative states for passenger agents
 */
public enum EPassengerState
{

    /**
     * the passenger is standing around at a station and doing nothing (for whatever reason, probably they don't know what to do, empty itinerary...?! or
     * arrived at their destination)
     */
    IDLE_AT_STATION,
    /**
     * the passenger is standing on a particular platform at the station, waiting for the next train in their itinerary (technically, waiting for the platform
     * to notify them that the train is there, upon which they will change into IN_ENTRANCE_QUEUE of a randomly selected door and register with the door, and
     * unsubscribe from the platform)
     */
    ON_PLATFORM_WAITING_FOR_TRAIN,
    /**
     * the passenger is moving through the station in order to get onto the platform from which the next train in their itinerary shall depart according to
     * their knowledge. after the duration of this process, the state changes to ON_PLATFORM_WAITING_FOR_TRAIN, and registering with the platform in order
     * to be notified when the train arrives (which may be an immediate response if the train is already there).
     */
    MOVING_THROUGH_STATION,
    /**
     * the passenger is standing in line to enter the next train in their itinerary, which has arrived.
     * they remain passive until the door notifies them that it's their turn to step in, upon which the state changes to ENTERING_TRAIN.
     */
    IN_ENTRANCE_QUEUE,
    /**
     * the passenger is in the process of boarding the next train in their itinerary.
     * after the duration of this process, they will be ON_TRAIN. upon exiting from this state, the passenger will inform the door that they finished entering
     * (so that the next passenger in the entry or exit queue can step in or out, respectively)
     */
    ENTERING_TRAIN,
    /**
     * the passenger is riding on the current train in their itinerary, waiting for arrival at the station where they have to get out, upon which
     * the state changes to IN_EXIT_QUEUE with a notification to a particular door of the train they are on (chosen randomly).
     */
    ON_TRAIN,
    /**
     * the passenger is standing in line to alight from the current train in their itinerary, which has already arrived at the station at which they have
     * to get out. they remain passive until the door notifies them that it's their turn to step out, upon which the state changes to LEAVING_TRAIN.
     */
    IN_EXIT_QUEUE,
    /**
     * the passenger is in the process of alighting from the current train in their itinerary.
     * after the duration of this process, they will be either MOVING_THROUGH_STATION or ON_PLATFORM_WAITING_FOR_TRAIN (registering with the platform),
     * depending on whether the next train in their itinerary, if any, will depart from the same track where they arrived or not.
     * upon exiting from this process, the passenger will inform the door that they finished leaving (so that the next passenger in the entry or exit queue can
     * step in or out, respectively)
     */
    LEAVING_TRAIN;

}
