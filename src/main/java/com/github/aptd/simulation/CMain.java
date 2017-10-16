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
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

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
        l_clioptions.addOption( "interactive", false, "start web server for interactive execution" );
        l_clioptions.addOption( "sequential", false, "run simulation in sequential (default is parallel)" );
        l_clioptions.addOption( "iteration", true, "number of iterations" );
        l_clioptions.addOption( "scenariotype", true, "comma-separated list of scenario types (default: xml)" );
        l_clioptions.addOption( "scenario", true, "comma-separated list of scenario files" );
        l_clioptions.addOption( "timemodel", true, "jump for jumping time, step for stepping time (default: step)" );
        l_clioptions.addOption( "passengerspeedseed", true, "seed for uniform random generator of passenger speeds (default: 1)" );
        l_clioptions.addOption( "passengerspeedmin", true, "minimum value for uniform random generator of passenger speeds (default: 1.5)" );
        l_clioptions.addOption( "passengerspeedmax", true, "maximum value for uniform random generator of passenger speeds (default: 1.5)" );
        l_clioptions.addOption( "numberofpassengers", true, "number of passengers (default: 20)" );
        l_clioptions.addOption( "lightbarrierminfreetime", true, "minimum duration how long the light barrier has to be free before the door can close (default: 3)" );
        l_clioptions.addOption( "delayseconds", true, "primary delay of first train in seconds (default: 0)" );

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

        execute( l_cli );
    }

    private static void execute( final CommandLine p_cli )
    {
        final double l_passengerspeedmin = Double.parseDouble( p_cli.getOptionValue( "passengerspeedmin", "1.5" ) );
        final double l_passengerspeedmax = Double.parseDouble( p_cli.getOptionValue( "passengerspeedmax", "1.5" ) );
        final int l_numberofpassengers = Integer.parseUnsignedInt( p_cli.getOptionValue( "numberofpassengers", "20" ) );
        final double l_lightbarrierminfreetime = Double.parseDouble( p_cli.getOptionValue( "lightbarrierminfreetime", "3.0" ) );
        final double l_delayseconds = Double.parseDouble( p_cli.getOptionValue( "delayseconds", "0.0" ) );

        // load configuration
        CConfiguration.INSTANCE.loadfile( p_cli.getOptionValue( "config", "" ) );

        // execute experiments in batch processing and starts http server
        new Thread( () -> datamodel( p_cli )
            .map( i -> i.getLeft().model().get(

                EFactory.from( CConfiguration.INSTANCE.getOrDefault( "local", "runtime", "type" ) ).factory(),

                i.getRight(),

                p_cli.hasOption( "iteration" )
                ? Long.parseLong( p_cli.getOptionValue( "iteration" ) )
                : (long) CConfiguration.INSTANCE.getOrDefault( 0, "default", "iteration" ),

                !p_cli.hasOption( "sequential" ) && CConfiguration.INSTANCE.<Boolean>getOrDefault( true, "runtime", "parallel" ),

                p_cli.hasOption( "timemodel" )
                ? p_cli.getOptionValue( "timemodel" )
                : "step",

                () ->
                {
                    if ( l_passengerspeedmax <= l_passengerspeedmin ) return new ConstantRealDistribution( l_passengerspeedmin );
                    final RandomGenerator l_randomgenerator = new JDKRandomGenerator();
                    l_randomgenerator.setSeed( Long.parseLong( p_cli.getOptionValue( "passengerspeedseed", "1" ) ) );
                    return new UniformRealDistribution( l_randomgenerator, l_passengerspeedmin, l_passengerspeedmax );
                },

                l_numberofpassengers, l_lightbarrierminfreetime, l_delayseconds
            ) )
            .forEach( i -> ERuntime.LOCAL.get().execute( i ) )
        ).start();

        // start http server if possible
        if ( p_cli.hasOption( "interactive" ) ) CHTTPServer.execute();
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
