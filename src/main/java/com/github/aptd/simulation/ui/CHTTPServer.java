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
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.InetSocketAddress;



/**
 * Jersey-HTTP server for UI
 *
 * @see https://dzone.com/articles/standalone-java-application-with-jersey-and-jetty
 * @see http://www.dropwizard.io/1.0.5/docs/getting-started.html#jetty-for-http
 * @see https://www.acando.no/thedailypassion/200555/a-rest-service-with-jetty-and-jersey
 * @see https://www.mkyong.com/webservices/jax-rs/jersey-hello-world-example/
 * @see http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
 * @see https://github.com/jetty-project/embedded-jetty-jsp/blob/master/src/main/java/org/eclipse/jetty/demo/Main.java
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
        l_webapp.setServer( l_server );
        l_webapp.setDescriptor( "web-inf/web.xml" );
        l_server.setHandler( l_webapp );
        l_webapp.setWar( "src/main/webapp" );

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
