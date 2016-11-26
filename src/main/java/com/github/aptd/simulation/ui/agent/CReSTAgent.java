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

package com.github.aptd.simulation.ui.agent;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * jersy structure of agent structure
 *
 * @see https://www.mkyong.com/webservices/jax-rs/json-example-with-jersey-jackson/
 * @see https://jersey.java.net/documentation/1.19.1/json.html
 */
@XmlRootElement( name = "agent" )
public final class CReSTAgent<T> implements IReSTAgent
{
    /**
     * cycle
     */
    private long m_cycle;
    /**
     * sleeping count
     */
    private long m_sleeping;
    /**
     * agent id
     */
    private T m_id;

    /**
     * get cycle
     * @return cycle
     */
    public final long getCycle()
    {
        return m_cycle;
    }

    /**
     * set cycle
     * @param p_cycle cycle
     * @return self reference
     */
    @XmlElement( name = "cycle" )
    public final CReSTAgent setCycle( final long p_cycle )
    {
        m_cycle = p_cycle;
        return this;
    }

    /**
     * get sleeping
     * @return sleeping count
     */
    public final long getSleeping()
    {
        return m_sleeping;
    }

    /**
     * set sleeping
     * @param p_sleeping sleeping value
     * @return self reference
     */
    @XmlElement( name = "sleeping" )
    public final CReSTAgent setSleeping( final long p_sleeping )
    {
        m_sleeping = p_sleeping;
        return this;
    }

    /**
     * get name / id
     * @return name / id
     */
    public final T getID()
    {
        return m_id;
    }

    /**
     * set name / id
     * @param p_id name / id
     * @return self reference
     */
    @XmlElement( name = "id" )
    public final CReSTAgent setID( final T p_id )
    {
        m_id = p_id;
        return this;
    }

}
