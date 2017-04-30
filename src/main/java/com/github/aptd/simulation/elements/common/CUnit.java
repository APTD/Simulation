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

package com.github.aptd.simulation.elements.common;

import com.github.aptd.simulation.common.CConfiguration;


/**
 * simulation units
 *
 * @todo refactor
 */
public final class CUnit
{
    /**
     * singletone instance
     */
    public static final CUnit INSTANCE = new CUnit();
    /**
     * constant value to scale km/h in m/s
     **/
    private static final double KMINMS = 1000.0 / 3600.0;
    /**
     * constant value to scale m/s in km/h
     **/
    private static final double MSINKMH = 1 / KMINMS;
    /**
     * cell size in meter
     */
    private final double m_cellsize = CConfiguration.INSTANCE.<Number>getOrDefault( 1.5, "units", "cellsize_in_meter" ).doubleValue();
    /**
     * timestep in seconds
     */
    private final double m_timestep = CConfiguration.INSTANCE.<Number>getOrDefault( 0.5, "units", "time_in_minutes" ).doubleValue();
    /**
     * meter which can be moved at one timestep
     */
    private final double m_distancetimestep = KMINMS * m_timestep;
    /**
     * number of cells which can be moved at one timestep
     */
    private final int m_celltimestep = (int) Math.floor( m_distancetimestep / m_cellsize );



    /**
     * private ctor
     */
    private CUnit()
    {}


    /**
     * returns the speed change in one timestep
     *
     * @param p_acceleration acceleration or deceleration in m/sec^2
     * @return speed change in km/h
     */
    public final double accelerationtospeed( final double p_acceleration )
    {
        return p_acceleration * MSINKMH * m_timestep;
    }

    /**
     * returns the cell size
     *
     * @return cell size in meter
     */
    public final double cellsize()
    {
        return m_cellsize;
    }

    /**
     * returns cell number to distance in meter
     *
     * @param p_cells cell number
     * @return meter value
     */
    public final double celltometer( final int p_cells )
    {
        return p_cells * m_cellsize;
    }

    /**
     * returns cell number to distance in kilometer
     *
     * @param p_cells cell number
     * @return kilometer value
     */
    public final double celltokilometer( final int p_cells )
    {
        return this.celltometer( p_cells ) / 1000.0;
    }

    /**
     * returns the speed for traveling the given distance in one timestep
     *
     * @param p_distance distance in meter
     * @return speed in km/h
     */
    public final int speedofdistance( final double p_distance )
    {
        return (int) ( p_distance / m_timestep *  MSINKMH );
    }

    /**
     * returns the speed in cell positions at one timestep
     *
     * @param p_speed speed in km/h
     * @return amount of cells / timestep
     */
    public final int speedtocell( final int p_speed )
    {
        return p_speed * m_celltimestep;
    }

    /**
     * returns the distance in meter which
     * can moved in one timestep
     *
     * @param p_speed speed in km/h
     * @return meter
     */
    public final double speedtodistance( final int p_speed )
    {
        return p_speed *  MSINKMH  * m_timestep;
    }

    /**
     * returns the timestep in seconds
     *
     * @return timestep
     */
    public final double time()
    {
        return m_timestep;
    }

    /**
     * returns the time in seconds for n steps
     *
     * @param p_step step number
     * @return time in seconds
     */
    public final double time( final int p_step )
    {
        return m_timestep * p_step;
    }

    /**
     * returns the time in minutes for n steps
     *
     * @param p_step step number
     * @return time in minutes
     */
    public final double timeinminutes( final int p_step )
    {
        return this.time( p_step ) / 60.0;
    }
}
