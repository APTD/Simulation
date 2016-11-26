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


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.lightjason.agentspeak.agent.IAgent;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.function.Function;


/**
 * singleton webservice provider to control
 * an agent as XML and JSON request
 */
@Singleton
@Path( "/agent/{id}" )
public final class CProvider
{
    /**
     * singleton instance
     */
    public static final CProvider INSTANCE = new CProvider();
    /**
     * function to format agent identifier
     */
    private final Function<String, String> m_formater = (i) -> i.trim().toLowerCase( Locale.ROOT );
    /**
     * map with agents
     **/
    private final BiMap<String, IAgent<?>> m_agents = Maps.synchronizedBiMap( HashBiMap.create() );

    /**
     * ctor
     */
    private CProvider()
    {}

    /**
     * http get for an agent
     *
     * @param p_id agent identifier
     * @return agent object
     */
    @GET
    @Path( "/mind" )
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public final IReSTAgent mind( @PathParam( "id" ) final String p_id )
    {
        final String l_id = m_formater.apply( p_id );
        final IAgent<?> l_agent = m_agents.get( l_id );
        return l_agent == null
               ? null
               : l_agent.inspect( new CReSTInspector( l_id ) ).findFirst().orElseThrow( RuntimeException::new ).get();
    }

    /**
     * register an agent with a name
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @return self reference
     */
    public final CProvider register( final String p_id, final IAgent<?> p_agent )
    {
        m_agents.put( m_formater.apply( p_id ), p_agent );
        return this;
    }

    /**
     * unregister agent by the name
     *
     * @param p_id agent name / id (case insensitive )
     * @return self refrence
     */
    public final CProvider unregister( final String p_id )
    {
        m_agents.remove( m_formater.apply( p_id ) );
        return this;
    }

    /**
     * unregister agent by the objct
     *
     * @param p_agent agent object
     * @return self refrence
     */
    public final CProvider unregister( final IAgent<?> p_agent )
    {
        m_agents.inverse().remove( p_agent );
        return this;
    }


}
