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

package com.github.aptd.simulation.elements;

import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.generator.IAgentGenerator;

import java.time.Instant;


/**
 * any object interface
 *
 * @tparam T domain specific type
 */
public interface IElement<T extends IAgent<?>> extends IPerceive, IAgent<T>
{

    /**
     * name of the object
     *
     * @return string name
     */
    String id();


    /**
     * Get the next time this agent will become active
     *
     * @return current time if agent is still active, next scheduled trigger time instant, or Instant.MAX if none
     */
    public Instant nextActivation();


    /**
     * generator interface
     *
     * @tparam T element generator
     */
    interface IGenerator<T extends IElement<?>> extends IAgentGenerator<T>
    {
        /**
         * resets the internal counter
         *
         * @return self-reference
         */
        IGenerator<T> resetcount();
    }

}
