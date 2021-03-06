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

package com.github.aptd.simulation.ui;

import com.github.aptd.simulation.common.CCommon;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.error.CRuntimeException;
import com.github.aptd.simulation.error.CSemanticException;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.lightjason.rest.CApplication;

import javax.servlet.DispatcherType;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.MessageFormat;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.stream.Stream;



/**
 * Jersey-Jetty-HTTP server for UI
 *
 * debug: -Dorg.eclipse.jetty.servlet.LEVEL=ALL
 *
 */
public final class CHTTPServer
{
    /**
     * webservcer instance
     */
    private static final CHTTPServer INSTANCE = CConfiguration.INSTANCE.<Boolean>getOrDefault( false, "httpserver", "enable" ) ? new CHTTPServer() : null;
    /**
     * server instance
     */
    private final Server m_server;
    /**
     * REST-API application
     */
    private final CApplication m_restagent = new CApplication();

    /**
     * ctor
     */
    private CHTTPServer()
    {
        // web context definition
        final WebAppContext l_webapp = new WebAppContext();

        // server process
        m_server = new Server(
            new InetSocketAddress( CConfiguration.INSTANCE.<String>getOrDefault( "localhost", "httpserver", "host" ),
                                   CConfiguration.INSTANCE.<Integer>getOrDefault( 8000, "httpserver", "port" )
            )
        );

        // set server / webapp connection
        m_server.setHandler( l_webapp );
        l_webapp.setServer( m_server );
        l_webapp.setContextPath( "/" );
        l_webapp.setWelcomeFiles( new String[]{"index.html", "index.htm"} );
        l_webapp.setResourceBase(
            CHTTPServer.class.getResource(
                MessageFormat.format( "/{0}/html", CCommon.PACKAGEROOT.replace( ".", "/" ) )
            ).toExternalForm()
        );
        l_webapp.addServlet( new ServletHolder( new ServletContainer( m_restagent ) ), "/rest/*" );
        l_webapp.addFilter( new FilterHolder( new CrossOriginFilter() ), "/rest/*", EnumSet.of( DispatcherType.REQUEST ) );
    }


    /**
     * execute the server instance
     */
    public static void execute()
    {
        if ( INSTANCE == null )
            return;

        try
        {
            INSTANCE.m_server.start();

            // open browser if possible
            if ( ( CConfiguration.INSTANCE.<Boolean>getOrDefault( true, "openbrowser" ) ) && ( Desktop.isDesktopSupported() ) )
                Desktop.getDesktop().browse( new URI(
                    "http://" + CConfiguration.INSTANCE.<String>getOrDefault( "localhost", "httpserver", "host" )
                        + ":" + CConfiguration.INSTANCE.<Integer>getOrDefault( 8000, "httpserver", "port" )
                    )
                );

            INSTANCE.m_server.join();
        }
        catch ( final InterruptedException | URISyntaxException | IOException l_exception )
        {
            throw new CSemanticException( l_exception );
        }
        catch ( final Exception l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
        finally
        {
            INSTANCE.m_server.destroy();
        }
    }

    /**
     * register agent if server is started
     *
     * @param p_agent agent object
     * @param p_group additional group
     * @return agent object
     */
    public static <T extends IElement<?>> T register( final T p_agent, final String... p_group )
    {
        if ( INSTANCE == null )
            return p_agent;

        INSTANCE.m_restagent.register( p_agent.id(), p_agent, p_group );

        return p_agent;
    }

    /**
     * register agent if server started
     *
     * @param p_agentgroup tupel of agent and stream of group names
     * @return agent object
     * @tparam T agent type
     */
    public static <T extends IElement<?>> T register( final Pair<T, Stream<String>> p_agentgroup )
    {
        return register( p_agentgroup.getLeft(), p_agentgroup.getRight().toArray( String[]::new ) );
    }

}
