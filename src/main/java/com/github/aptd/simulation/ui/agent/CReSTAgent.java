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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * container for the restful export
 */
@XmlRootElement( name = "agent" )
public final class CReSTAgent<T> implements IReSTAgent
{
    /**
     * storage element
     */
    private final Map<String, Object> m_storage = new HashMap<>();
    /**
     * running plans
     */
    private final List<String> m_runningplan = new ArrayList<>();
    /**
     * belief as strings
     */
    private final List<String> m_belief = new ArrayList<>();
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
    @XmlElement( name = "cycle" )
    public final long getCycle()
    {
        return m_cycle;
    }

    /**
     * set cycle
     * @param p_cycle cycle
     * @return self reference
     */
    public final CReSTAgent setCycle( final long p_cycle )
    {
        m_cycle = p_cycle;
        return this;
    }

    /**
     * get sleeping
     * @return sleeping count
     */
    @XmlElement( name = "sleeping" )
    public final long getSleeping()
    {
        return m_sleeping;
    }

    /**
     * set sleeping
     * @param p_sleeping sleeping value
     * @return self reference
     */
    public final CReSTAgent setSleeping( final long p_sleeping )
    {
        m_sleeping = p_sleeping < 0 ? 0 : p_sleeping;
        return this;
    }

    /**
     * get name / id
     * @return name / id
     */
    @XmlElement( name = "id" )
    public final T getID()
    {
        return m_id;
    }

    /**
     * set name / id
     * @param p_id name / id
     * @return self reference
     */
    public final CReSTAgent setID( final T p_id )
    {
        m_id = p_id;
        return this;
    }

    /**
     * returns belief list
     *
     * @return beliefs
     */
    @XmlElementWrapper( name = "beliefs" )
    @XmlElement( name = "belief" )
    public final List<String> getBelief()
    {
        return m_belief;
    }

    /**
     * sets a belief
     *
     * @param p_belief belief as string
     * @return self reference
     */
    public final CReSTAgent setBelief( final String p_belief )
    {
        m_belief.add( p_belief );
        return this;
    }

    /**
     * returns the running plans
     *
     * @return list with running plans
     */
    @XmlElementWrapper( name = "runningplans" )
    @XmlElement( name = "plan" )
    public final List<String> getRunningplan()
    {
        return m_runningplan;
    }

    /**
     * sets the running plans
     *
     * @param p_plan plan
     * @return self reference
     */
    public final CReSTAgent setRunningplan( final String p_plan )
    {
        m_runningplan.add( p_plan );
        return this;
    }

    /**
     * returns the storage map
     * @return storage map
     */
    @XmlElement( name = "storage" )
    public final Map<String, ?> getStorage()
    {
        return m_storage;
    }

    /**
     * sets a storage item
     *
     * @param p_value storage entry
     * @return self reference
     */
    public final CReSTAgent setStorage( final Map.Entry<String, ?> p_value )
    {
        m_storage.put( p_value.getKey(), p_value.getValue() );
        return this;
    }

}
