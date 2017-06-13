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

package com.github.aptd.simulation.core.time.local;

import com.github.aptd.simulation.core.time.IBaseTime;

import java.time.Instant;
import java.time.temporal.TemporalAmount;


/**
 * Time mechanism for discrete time advancing with simulation steps (one advance per "call()" invocation)
 */
public class CStepTime extends IBaseTime
{

    private TemporalAmount m_stepsize;

    /**
     * Create a new instance with given stepsize
     *
     * @param p_starttime initial time
     * @param p_stepsize how long is one simulation cycle in simulated time
     */
    public CStepTime( final Instant p_starttime, final TemporalAmount p_stepsize )
    {
        m_currenttime = p_starttime;
        m_stepsize = p_stepsize;
    }

    /**
     * advance the time by one step
     *
     * @return self-reference
     */
    public CStepTime call()
    {
        super.call();
        m_currenttime = m_currenttime.plus( m_stepsize );
        return this;
    }

    /**
     * get the step size
     *
     * @return step size
     */
    public TemporalAmount stepsize()
    {
        return m_stepsize;
    }

    /**
     * set a new step size
     *
     * @param p_stepsize new step size
     * @return self-reference
     */
    public CStepTime stepsize( final TemporalAmount p_stepsize )
    {
        m_stepsize = p_stepsize;
        return this;
    }

}
