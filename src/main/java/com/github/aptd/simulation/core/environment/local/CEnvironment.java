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

package com.github.aptd.simulation.core.environment.local;

import com.github.aptd.simulation.core.environment.IEnvironment;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IBaseElement;
import org.lightjason.agentspeak.language.ILiteral;

import java.util.stream.Stream;


/**
 * environment class
 */
public final class CEnvironment implements IEnvironment
{

    private ITime m_time;

    public ITime time()
    {
        return m_time;
    }

    /**
     * set the current simulated time
     *
     * @param p_time the new current time
     * @return self
     */
    public CEnvironment time( final ITime p_time )
    {
        m_time = p_time;
        return this;
    }

    @Override
    public final <T extends IBaseElement<?>> T initializeagent( final T p_agent )
    {
        return p_agent;
    }


    @Override
    public final Stream<ILiteral> literal( final IBaseElement<?> p_agent )
    {
        return Stream.of();
    }


}
