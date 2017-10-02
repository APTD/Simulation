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
import com.github.aptd.simulation.core.time.ITime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Time mechanism for discrete time advancing with simulation steps (one advance per "call()" invocation)
 */
public final class CStepTime extends IBaseTime
{
    /**
     * time step size
     */
    private final AtomicReference<TemporalAmount> m_stepsize;


    /**
     * ctor
     *
     * @param p_starttime initial time
     * @param p_stepsize how long is one simulation cycle in simulated time
     */
    public CStepTime( final Instant p_starttime, final TemporalAmount p_stepsize, final long p_steplimit )
    {
        this( p_starttime, p_stepsize, p_steplimit, ZoneId.systemDefault() );
    }

    /**
     * Create a new instance with given stepsize
     *
     * @param p_starttime initial time
     * @param p_stepsize how long is one simulation cycle in simulated time
     * @param p_zone time zone
     */
    public CStepTime( final Instant p_starttime, final TemporalAmount p_stepsize, final long p_steplimit, final ZoneId p_zone )
    {
        super( p_starttime, p_steplimit, p_zone );
        m_stepsize = new AtomicReference<>( p_stepsize );
    }


    @Override
    public final ITime call() throws Exception
    {
        m_currenttime.set( m_currenttime.get().plus( m_stepsize.get() ) );
        return super.call();
    }

    /**
     * get the step size
     *
     * @return step size
     */
    public final TemporalAmount stepsize()
    {
        return m_stepsize.get();
    }

    /**
     * set a new step size
     *
     * @param p_stepsize new step size
     * @return self-reference
     */
    public final ITime stepsize( final TemporalAmount p_stepsize )
    {
        m_stepsize.set( p_stepsize );
        return this;
    }

}
