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

package com.github.aptd.simulation.core.units;

/**
 * unit class
 */
public final class CUnit
{
    /**
     * constant value to scale km/h in m/s
     **/
    private static final double KMHINMS = 1000.0 / 3600.0;
    /**
     * constant value to scale m/s in km/h
     **/
    private static final double MSINKMH = 1 / KMHINMS;
    /**
     * cell size in meter
     */
    private final double m_cellsize;
    /**
     * timestep in seconds
     */
    private final double m_timestep;
    /**
     * number of cells which can be moved at one timestep
     */
    private final double m_celltimestep;


    /**
     * ctor
     *
     * @param p_cellsize cell size
     * @param p_timestep timestep size
     */
    public CUnit( final Number p_cellsize, final Number p_timestep )
    {
        m_cellsize = p_cellsize.doubleValue();
        m_timestep = p_timestep.doubleValue();
        m_celltimestep = KMHINMS * m_timestep / m_cellsize;
    }


    /**
     * returns the speed change in one timestep
     *
     * @param p_acceleration acceleration or deceleration in m/sec^2
     * @return speed change in km/h
     */
    public final Number accelerationtospeed( final Number p_acceleration )
    {
        return p_acceleration.doubleValue() * MSINKMH * m_timestep;
    }

    /**
     * returns the cell size
     *
     * @return cell size in meter
     */
    public final Number cellsize()
    {
        return m_cellsize;
    }

    /**
     * returns cell number to distance in meter
     *
     * @param p_cells cell number
     * @return meter value
     */
    public final Number celltometer( final Number p_cells )
    {
        return p_cells.doubleValue() * m_cellsize;
    }

    /**
     * returns cell number to distance in kilometer
     *
     * @param p_cells cell number
     * @return kilometer value
     */
    public final Number celltokilometer( final Number p_cells )
    {
        return this.celltometer( p_cells ).doubleValue() / 1000D;
    }

    /**
     * returns the speed for traveling the given distance in one timestep
     *
     * @param p_distance distance in meter
     * @return speed in km/h
     */
    public final Number speedofdistance( final Number p_distance )
    {
        return p_distance.doubleValue() / m_timestep * MSINKMH;
    }

    /**
     * returns the speed in cell positions at one timestep
     *
     * @param p_speed speed in km/h
     * @return amount of cells / timestep
     */
    public final Number speedtocell( final Number p_speed )
    {
        return p_speed.doubleValue() * m_celltimestep;
    }

    /**
     * returns the distance in meter which
     * can moved in one timestep
     *
     * @param p_speed speed in km/h
     * @return meter
     */
    public final Number speedtodistance( final Number p_speed )
    {
        return p_speed.doubleValue() * KMHINMS * m_timestep;
    }

    /**
     * returns the timestep in seconds
     *
     * @return timestep
     */
    public final Number time()
    {
        return m_timestep;
    }

    /**
     * returns the time in seconds for n steps
     *
     * @param p_step step number
     * @return time in seconds
     */
    public final Number time( final Number p_step )
    {
        return p_step.doubleValue() * m_timestep;
    }

    /**
     * returns the time in minutes for n steps
     *
     * @param p_step step number
     * @return time in minutes
     */
    public final Number timeinminutes( final Number p_step )
    {
        return this.time( p_step ).doubleValue() / 60D;
    }
}
