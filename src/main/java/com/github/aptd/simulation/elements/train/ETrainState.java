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

/**
 * enum of qualitative states for train agents
 */
public enum ETrainState
{

    /**
     * the train is standing at a station and either its minimum waiting time is not yet over or the next departure time has not been reached
     */
    ARRIVED,
    /**
     * the train is ready to drive and waiting for preconditions to be fulfilled (such as all doors closed and locked, signals permitting)
     */
    WAITING_TO_DRIVE,
    /**
     * the train is in the process of driving
     */
    DRIVING;

}
