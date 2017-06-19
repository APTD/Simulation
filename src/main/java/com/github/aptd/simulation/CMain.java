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


import com.codepoetics.protonpack.StreamUtils;
import com.github.aptd.simulation.common.CCommon;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.core.runtime.ERuntime;
import com.github.aptd.simulation.datamodel.EDataModel;
import com.github.aptd.simulation.factory.EFactory;
import com.github.aptd.simulation.ui.CHTTPServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


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
        l_clioptions.addOption( "config", true, "path to configuration directory (default: <user home>/.asimov/configuration.yaml)" );
        l_clioptions.addOption( "sequential", false, "run simulation in sequential (default is parallel)" );
        l_clioptions.addOption( "iteration", true, "number of iterations" );
        l_clioptions.addOption( "scenariotype", true, "comma-separated list of scenario types (default: xml)" );
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

        if ( !l_cli.hasOption( "scenario" ) )
        {
            System.out.println( CCommon.languagestring( CMain.class, "noscenario", CConfiguration.createdefault() ) );
            System.exit( -1 );
            return;
        }

        // load configuration
        CConfiguration.INSTANCE.loadfile( l_cli.getOptionValue( "config", "" ) );

        // execute experiments in batch processing and starts http server
        new Thread( () -> datamodel( l_cli )
            .map( i -> i.getLeft().model().get(

                EFactory.from( CConfiguration.INSTANCE.getOrDefault( "local", "runtime", "type" ) ).factory(),

                i.getRight(),

                l_cli.hasOption( "iteration" )
                ? Long.parseLong( l_cli.getOptionValue( "iteration" ) )
                : (long) CConfiguration.INSTANCE.getOrDefault( 0, "default", "iteration" ),

                !l_cli.hasOption( "sequential" ) && CConfiguration.INSTANCE.<Boolean>getOrDefault( true, "runtime", "parallel" )

            ) )
            .forEach( i -> ERuntime.LOCAL.get().execute( i ) )
        ).start();

        // start http server if possible
        CHTTPServer.execute();
    }


    /**
     * returns the experiment data models
     *
     * @param p_options commandline options
     * @return stream of experiments
     */
    private static Stream<Pair<EDataModel, String>> datamodel( final CommandLine p_options )
    {
        final List<String> l_instances = Arrays.stream( p_options.getOptionValue( "scenario" ).split( "," ) )
                                               .map( String::trim )
                                               .filter( i -> !i.isEmpty() )
                                               .collect( Collectors.toList() );

        final List<String> l_types = Arrays.stream( p_options.getOptionValue( "scenariotype", "" ).split( "," ) )
                                            .map( String::trim )
                                            .filter( i -> !i.isEmpty() )
                                            .collect( Collectors.toList() );

        return StreamUtils.zip(
            l_instances.stream(),
            Stream.concat(
                l_types.stream(),
                IntStream.range( 0, l_instances.size() - l_types.size() )
                         .mapToObj( i -> CConfiguration.INSTANCE.getOrDefault( "xml", "default", "datamodel" ) )
            ),
            ( i, j ) -> new ImmutablePair<>( EDataModel.from( j ), i )
        );
    }



}
