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
 * enum of qualitative states for door agents
 */
public enum EDoorState
{

    /**
     * The door is closed and cannot be opened unless unlocked. This is the required state for all doors to be in before a train can depart.
     * It can only be unlocked by the train, which results in the CLOSED_RELEASED or in the OPENING state if there are already passengers in either queue.
     */
    CLOSED_LOCKED,
    /**
     * The door is still closed but can be opened when somebody wants to enter or exit. It can be locked by the train into the CLOSED_LOCKED state,
     * or opened by the train or the enqueuing of a passenger, so it transits to the OPENING state.
     */
    CLOSED_RELEASED,
    /**
     * The door is in the process of opening. A real-valued (floating point) variable tracks how far it is open at the moment
     * (using on-demand updates in the usual fashion of engaging continuous processes within the discrete-event setting).
     * If uninterrupted, this is a volatile state leading to the OPEN_CLOSEABLE or OPEN_BUSY state, i.e., this state has to be thought of
     * as combined with the queues' states.
     */
    OPENING,
    /**
     * The door is open and not currently used (queues are empty), but was used so recently that it cannot be closed right now.
     * The amount of time since the last use is tracked by a real-valued (floating point) variable.
     * If uninterrupted, this state transitions into the OPEN_CLOSEABLE state.
     * It can be interrupted into the OPEN_FREE_SHALL_CLOSE state, keeping the value of the amount of time since the last use,
     * or into the OPEN_BUSY state by the enqueueing of a passenger (into one of the queues, which are both empty in this state).
     */
    OPEN_FREE,
    /**
     * The door is open and currently in use by a passenger. It waits for a notification from the passenger that they finished using it.
     * Then, if there is any passenger in either queue, it remains in the OPEN_BUSY state and lets the next one use it.
     * Otherwise, the door changes into the OPEN_FREE state.
     */
    OPEN_BUSY,
    /**
     * The door is open and not currently used (queues are empty), and hasn't been used long enough so it could be closed.
     * This state can be changed into CLOSING by a command from the train, or into the OPEN_BUSY state by the enqueueing of a passenger.
     */
    OPEN_CLOSEABLE,
    /**
     * The door is in the process of closing. A real-valued (floating point) variable tracks how far it is open at the moment.
     * If uninterrupted, this state leads to the CLOSED_RELEASED state.
     * This can be interrupted into the OPENING state by the enqueueing of a passenger, or by a command from the train.
     */
    CLOSING,
    /**
     * Like the OPENING state, but resulting in the OPEN_FREE_SHALL_CLOSE or OPEN_BUSY_SHALL_CLOSE state, depending on the queues' states.
     */
    OPENING_SHALL_CLOSE,
    /**
     * Like the OPEN_FREE state, but resulting in the CLOSING_LOCKED state instead of the OPEN_CLOSEABLE state, or, if interrupted,
     * in the OPEN_BUSY_SHALL_CLOSE state instead of the OPEN_BUSY state. It can change into the OPEN_FREE state by a release command from the train.
     */
    OPEN_FREE_SHALL_CLOSE,
    /**
     * Like the OPEN_BUSY state, but remaining in the OPEN_BUSY_SHALL_CLOSE state instead of remaining in the OPEN_BUSY state, or
     * changing to the CLOSING_LOCKED state when queues are empty. It can change into the OPEN_BUSY state by a release command from the train.
     */
    OPEN_BUSY_SHALL_CLOSE,
    /**
     * Like the CLOSING state, but resulting in CLOSED_LOCKED instead of CLOSED_RELEASED.
     * Can be changed into CLOSING by a release command from the train. Can be changed into OPENING_SHALL_CLOSE by an aggressive passenger blocking the door.
     */
    CLOSING_LOCKED

}
