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

package com.github.aptd.simulation;


import com.github.aptd.simulation.common.CCommon;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * main class
 */
public final class CMain
{

    /**
     * ctor
     */
    private CMain()
    {}

    /**
     * main method
     * @param p_args command-line parameters
     * @throws IOException error on io errors
     */
    @SuppressWarnings( "unchecked" )
    public static void main( final String[] p_args ) throws IOException
    {
        // --- define CLI options ------------------------------------------------------------------------------------------------------------------------------

        final Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, "shows this information" );
        l_clioptions.addOption( "generateconfig", false, "generate default configuration" );
        l_clioptions.addOption( "generatescenario", false, "generate example scenario" );
        l_clioptions.addOption( "config", true, "path to configuration directory (default: <user home>/.asimov/configuration.yaml)" );
        l_clioptions.addOption( "scenario", true, "scenario configuration" );
        l_clioptions.addOption( "stepbystep", false, "runs simulation on manuell step-by-step execution" );

        final CommandLine l_cli;
        try
        {
            l_cli = new DefaultParser().parse( l_clioptions, p_args );
        }
        catch ( final Exception l_exception )
        {
            System.err.println( "command-line arguments parsing error" );
            System.exit( -1 );
            return;
        }



        // --- process CLI arguments and initialize configuration ----------------------------------------------------------------------------------------------

        if ( l_cli.hasOption( "help" ) )
        {
            new HelpFormatter().printHelp( new java.io.File( CMain.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getName(), l_clioptions );
            return;
        }

        if ( l_cli.hasOption( "generateconfig" ) )
        {
            System.out.println( CCommon.languagestring( CMain.class, "generateconfig", CConfiguration.createdefault() ) );
            return;
        }

        // load configuration and start the http server (if possible)
        CConfiguration.INSTANCE.loadfile( l_cli.getOptionValue( "config", "" ) );

        try
            (
                final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
            )
        {
            final IElement.IGenerator<?> l_generator = new CStation.CGenerator( l_station, CConfiguration.INSTANCE.agentaction(), CConfiguration.INSTANCE.agentaggregation() );

            l_generator.generatesingle( "Goettingen", 51.536777, 9.926074 );
            l_generator.generatesingle( "Hannover", 52.3745113, 9.741969 );

        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
        }

        // execute server
        CHTTPServer.execute();
    }

}
