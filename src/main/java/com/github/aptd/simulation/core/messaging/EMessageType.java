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

package com.github.aptd.simulation.core.messaging;

/**
 * types of messages
 */
public enum EMessageType
{
    TRAIN_TO_PLATFORM_ARRIVING,
    TRAIN_TO_PLATFORM_DEPARTING,
    PLATFORM_TO_PASSENGER_TRAINARRIVED,
    PLATFORM_TO_PASSENGER_TRAINDEPARTED,
    PASSENGER_TO_PLATFORM_SUBSCRIBE,
    PASSENGER_TO_PLATFORM_UNSUBSCRIBE,

    TRAIN_TO_DOOR_LOCK,
    TRAIN_TO_DOOR_UNLOCK,
    DOOR_TO_TRAIN_CLOSED_LOCKED,
    TRAIN_TO_DOOR_DEPARTING,

    TRAIN_TO_PASSENGER_ARRIVING,
    TRAIN_TO_PASSENGER_DEPARTING,
    PASSENGER_TO_TRAIN_SUBSCRIBE,
    PASSENGER_TO_TRAIN_UNSUBSCRIBE,

    PASSENGER_TO_DOOR_ENQUEUE_ENTRANCE,
    PASSENGER_TO_DOOR_ENQUEUE_EXIT,
    DOOR_TO_PASSENGER_YOURTURN,
    PASSENGER_TO_DOOR_FINISHED;
}
