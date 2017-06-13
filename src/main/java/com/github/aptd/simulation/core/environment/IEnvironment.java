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

package com.github.aptd.simulation.core.environment;

import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IBaseElement;
import org.lightjason.agentspeak.language.ILiteral;

import java.util.stream.Stream;

/**
 * interface to represent
 * an environment
 */
public interface IEnvironment
{


    /**
     * get the current time instant
     * @return the current time instant
     */
    ITime time();


    /**
     * set the current time
     * @param p_time the new current time
     * @return this environment (for chaining)
     */
    IEnvironment time( final ITime p_time );


    /**
     * is called if an agent is generated
     * (before the first agent cycle)
     *
     * @param p_agent agent
     * @return agent
     * @tparam T agent type
     */
    <T extends IBaseElement<?>> T initializeagent( final T p_agent );


    /**
     * returns the literal structure of
     * the environment based on the calling agent
     *
     * @param p_agent calling agent
     * @return literal stream
     */
    Stream<ILiteral> literal( final IBaseElement<?> p_agent );


}
