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


import com.github.aptd.simulation.common.CCommon;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;
import java.util.function.BiConsumer;
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
     * @return agent object or http error
     */
    @GET
    @Path( "/view" )
    //@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object view( @PathParam( "id" ) final String p_id )
    {
        final String l_id = m_formater.apply( p_id );
        final IAgent<?> l_agent = m_agents.get( l_id );
        return l_agent == null
               ? Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", l_id ) ).build()
               : l_agent.inspect( new CReSTInspector( l_id ) ).findFirst().orElseThrow( RuntimeException::new ).get();
    }

    /**
     * runs an agent cylce
     *
     * @return response
     */
    @GET
    @Path( "/cycle" )
    public final Object cycle( @PathParam( "id" ) final String p_id )
    {
        final String l_id = m_formater.apply( p_id );
        final IAgent<?> l_agent = m_agents.get( l_id );

        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", l_id ) ).build();

        try
        {
            l_agent.call();
        }
        catch ( final Exception l_exception )
        {
            return Response.status( Response.Status.CONFLICT ).entity( l_exception.getMessage() ).build();
        }
        return Response.status( Response.Status.OK ).build();
    }

    /**
     * triggers the agent immediately
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/trigger/{trigger}/immediately" )
    @Consumes( MediaType.APPLICATION_JSON )
    public final Object triggerimmediately( @PathParam( "id" ) final String p_id, @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        return this.executetrigger( p_id, p_trigger, p_literal, ( i, j ) -> i.trigger( j, true ) );
    }

    /**
     * triggers the agent
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/trigger/{trigger}" )
    @Consumes( MediaType.APPLICATION_JSON )
    public final Object trigger( @PathParam( "id" ) final String p_id, @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        return this.executetrigger( p_id, p_trigger, p_literal, ( i, j ) -> i.trigger( j ) );
    }

    /**
     * triggers the agent based on input data
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @param p_execute consumer function
     * @return response
     */
    private Response executetrigger( final String p_id, final String p_trigger, final String p_literal, final BiConsumer<IAgent<?>, ITrigger> p_execute )
    {
        // find agent
        final String l_id = m_formater.apply( p_id );
        final IAgent<?> l_agent = m_agents.get( l_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", l_id ) ).build();

        // parse literal
        final ILiteral l_literal;
        try
        {
            l_literal = CLiteral.parse( p_literal );
        }
        catch ( final Exception l_exception )
        {
            return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "literalparse" ) ).build();
        }

        // parse trigger
        final ITrigger l_trigger;
        switch ( p_trigger.toLowerCase( Locale.ROOT ) )
        {
            case "addgoal"      :
                l_trigger = CTrigger.from( ITrigger.EType.ADDGOAL, l_literal );
                break;

            case "deletegoal"   :
                l_trigger = CTrigger.from( ITrigger.EType.DELETEGOAL, l_literal );
                break;

            case "addbelief"    :
                l_trigger = CTrigger.from( ITrigger.EType.ADDBELIEF, l_literal );
                break;

            case "deletebelief" :
                l_trigger = CTrigger.from( ITrigger.EType.DELETEBELIEF, l_literal );
                break;

            default:
                return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "triggernotfound", p_trigger ) ).build();
        }

        // execute trigger
        p_execute.accept( l_agent, l_trigger );
        return Response.status( Response.Status.OK ).build();
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
