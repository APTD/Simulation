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
 * Jetty-HTTP server for UI
 *
 * @see http://www.eclipse.org/jetty/
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
        final WebAppContext l_webapp = new WebAppContext(
            this.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm(),
            "/"
        );
        l_webapp.setDescriptor( "web-inf/web.xml" );

        // server instance
        final Server l_server = new Server(
            new InetSocketAddress( CConfiguration.INSTANCE.<String>get( "httpserver", "host" ),
                                   CConfiguration.INSTANCE.<Integer>get( "httpserver", "port" )
            )
        );
        l_server.setHandler( l_webapp );

        try
        {
            l_server.start();
        }
        catch ( final Exception l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }

    /**
     * test
     */
    public static void execute()
    {
    }

}
