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

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.aptd.simulation.core.messaging.IMessage;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.generator.IAgentGenerator;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.stream.Stream;


/**
 * any object interface
 *
 * @tparam T domain specific type
 */
public interface IElement<T extends IAgent<?>> extends IPerceiveable, IAgent<T>, Callable<T>
{

    boolean imminent();

    Instant nextactivation();

    /**
     * name of the object
     *
     * @return string name
     */
    @JsonValue
    String id();

    /**
     * changes the environment of the agent
     *
     * @param p_environment environment
     * @return self reference
     * @todo should be moved - incomplete
     */
    IElement<T> environment( final IEnvironment<T, ?> p_environment );

    /**
     * get pending output messages
     *
     * @return Stream of messages
     */
    Stream<IMessage> output();

    T input( IMessage p_message );


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
