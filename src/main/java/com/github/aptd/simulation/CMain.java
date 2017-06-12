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
import com.github.aptd.simulation.core.runtime.ERuntime;
import com.github.aptd.simulation.datamodel.EDataModel;
import com.github.aptd.simulation.datamodel.IDataModel;
import com.github.aptd.simulation.ui.CHTTPServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;


/**
 * main class
 */
public final class CMain
{
    static
    {
        LogManager.getLogManager().reset();
    }

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
    public static void main( final String[] p_args ) throws IOException
    {
        // --- define CLI options ------------------------------------------------------------------------------------------------------------------------------

        final Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, "shows this information" );
        l_clioptions.addOption( "generateconfig", false, "generate default configuration" );
        l_clioptions.addOption( "generatescenario", false, "generate example scenario" );
        l_clioptions.addOption( "stepbystep", false, "runs simulation on manuell step-by-step execution" );
        l_clioptions.addOption( "sequential", false, "agents run in sequential order [default value: parallel]" );
        l_clioptions.addOption( "config", true, "path to configuration directory (default: <user home>/.asimov/configuration.yaml)" );
        l_clioptions.addOption( "scenario", true, "comma-separated list of scenario files" );

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



        // ---- replace by batch scenario process ----
        CHTTPServer.initialize();


        final List<IDataModel> l_scenario = Collections.unmodifiableList(
            Arrays.stream( l_cli.getOptionValue( "scenario", "" ).split( "," ) )
                  .map( String::trim )
                  .filter( i -> !i.isEmpty() )
                  .map( i -> datamodelbyfileextension( i ).get( i ) )
                  .collect( Collectors.toList() )
        );


        l_scenario.stream().forEach( d -> {
            ERuntime.from( "local" ).get().execute( d.get( null ) );
        } );

//        try
//            (
//                final InputStream l_station = new FileInputStream( "src/test/resources/asl/station.asl" );
//            )
//        {
//            final IEnvironment l_environment = EEnvironment.LOCAL.generate();
//            final IElement.IGenerator<?> l_generator = new CStation.CGenerator( l_station, CConfiguration.INSTANCE.agentaction(), l_environment );
//
//            l_generator.generatesingle( "Goettingen", 51.536777, 9.926074 );
//            l_generator.generatesingle( "Hannover", 52.3745113, 9.741969 );
//
//        }
//        catch ( final Exception l_exception )
//        {
//            l_exception.printStackTrace();
//        }
//
//        CHTTPServer.execute();
    }

    /**
     * returns the data-model factory based
     * on the file-extension
     *
     * @param p_file file name
     * @return data-model
     */
    private static EDataModel datamodelbyfileextension( final String p_file )
    {
        final String[] l_extension = p_file.split( "\\." );
        System.out.println( p_file );
        return EDataModel.from( l_extension[l_extension.length - 1] );
    }

}
