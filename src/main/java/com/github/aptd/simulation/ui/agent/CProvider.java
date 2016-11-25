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


import com.github.aptd.simulation.simulation.graph.local.CStation;
import com.github.aptd.simulation.simulation.graph.network.CStationGenerator;
import org.lightjason.agentspeak.agent.IAgent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * json agent provider
 *
 * @see https://myshadesofgray.wordpress.com/2014/04/27/restful-web-service-using-jersey-and-no-web-xml/
 * @see http://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
 * @see http://blog.dejavu.sk/2013/11/19/registering-resources-and-providers-in-jersey-2/
 * @see https://myshadesofgray.wordpress.com/2014/04/27/restful-web-service-using-jersey-and-no-web-xml/
 * @see http://stackoverflow.com/questions/5161466/how-do-i-use-the-jersey-json-pojo-support
 * @see http://stackoverflow.com/questions/17568469/jersey-2-0-equivalent-to-pojomappingfeature
 * @see https://github.com/jasonray/jersey-starterkit/wiki/Serializing-a-POJO-to-json-using-built-in-jersey-support
 * @see http://www.vogella.com/tutorials/REST/article.html
 * @see https://github.com/DominikAngerer/Boostraped-Jersey-RestAPI/blob/master/pom.xml
 */
@Path( "/agent/{id}" )
public final class CProvider
{
    /**
     * singleton instance
     */
    public static final CProvider INSTANCE = new CProvider();
    /** map with agents **/
    private final Map<String, IAgent<?>> m_agents = new ConcurrentHashMap<>();

    /**
     * ctor
     */
    public CProvider()
    {
        try
            (
                final InputStream l_station = new FileInputStream( "test/resource/asl/station.asl" );
            )
        {
            m_agents.put( "foo", new CStationGenerator<>( l_station, CStation.class ).generatesingle() );
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
        }
    }

    /**
     * http get for an agent
     *
     * @param p_id agent identifier
     * @return agent object
     */
    @GET
    @Path( "/mind" )
    @Produces( MediaType.APPLICATION_JSON )
    public final CAgent mind( @PathParam( "id" ) final String p_id )
    {
        final IAgent<?> l_agent = m_agents.get( format( p_id ) );
        return l_agent == null
               ? null
               : l_agent.inspect( new CInspector() ).findFirst().get().get();
    }

    /**
     * register an agent with a name
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @return serlf reference
     */
    public final CProvider register( final String p_id, final IAgent<?> p_agent )
    {
        m_agents.put( format( p_id ), p_agent );
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
        m_agents.remove( format( p_id ) );
        return this;
    }

    /**
     * formats the id
     *
     * @param p_value string
     * @return formated name
     */
    private static String format( final String p_value )
    {
        return p_value.trim().toLowerCase( Locale.ROOT );
    }

}
