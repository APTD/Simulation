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

import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.simulation.error.CSemanticException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.lightjason.rest.CApplication;

import java.net.InetSocketAddress;



/**
 * Jersey-Jetty-HTTP server for UI
 *
 */
public final class CHTTPServer
{
    /**
     * webservcer instance
     */
    private static final CHTTPServer INSTANCE = CConfiguration.INSTANCE.<Boolean>get( "httpserver", "enable" ) ? new CHTTPServer() : null;

    /**
     * ctor
     */
    private CHTTPServer()
    {
        // web context definition
        final WebAppContext l_webapp = new WebAppContext();

        // server process
        final Server l_server = new Server(
            new InetSocketAddress( CConfiguration.INSTANCE.<String>get( "httpserver", "host" ),
                                   CConfiguration.INSTANCE.<Integer>get( "httpserver", "port" )
            )
        );

        // set server / webapp connection
        l_server.setHandler( l_webapp );
        l_webapp.setServer( l_server );
        l_webapp.setContextPath( "/" );
        l_webapp.setWelcomeFiles( new String[]{"index.html", "index.htm"} );
        l_webapp.setResourceBase( CHTTPServer.class.getResource( "/com/github/aptd/simulation/html" ).toExternalForm() );
        l_webapp.addServlet( new ServletHolder( new ServletContainer( new CApplication() ) ), "/rest" );

        /*
            <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>org.lightjason.rest.CApplication</param-value>
            </init-param>
        */
        // http://stackoverflow.com/questions/27965207/creating-a-resourceconfig-that-behaves-the-same-way-as-default-jettys-jersey-re/27968094#27968094
        //l_webapp.setDefaultsDescriptor( CHTTPServer.class.getResource( "/com/github/aptd/simulation/web-inf/web.xml" ).toExternalForm() );
        //l_webapp.setWar( CMain.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm() );


        try
        {
            l_server.start();
            l_server.join();
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
        finally
        {
            l_server.destroy();
        }
    }

    /**
     * test
     */
    public static void execute()
    {
    }

}
