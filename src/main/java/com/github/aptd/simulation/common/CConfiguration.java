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

package com.github.aptd.simulation.common;

import com.github.aptd.simulation.simulation.error.CSemanticException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;


/**
 * configuration
 *
 * @todo check on hadoop access
 */
public final class CConfiguration
{
    /**
     * singleton instance
     */
    public static final CConfiguration INSTANCE = new CConfiguration();
    /**
     * map with configuration data
     */
    private final Map<String, Object> m_configuration = new ConcurrentHashMap<>();

    /**
     * ctor
     */

    private CConfiguration()
    {
    }

    /**
     * loads the configuration
     * @param p_path path elements
     * @return self reference
     */
    @SuppressWarnings( "unchecked" )
    public CConfiguration load( final String p_path )
    {
        m_configuration.clear();
        try
            (
                final InputStream l_stream = new FileInputStream( orDEfaultPath( p_path ) );
            )
        {
            m_configuration.putAll( (Map<String, Object>) new Yaml().load( l_stream ) );
        } catch ( final IOException l_exception )
        {
            throw new CSemanticException( l_exception );
        }

        return this;
    }

    /**
     * set default path
     *
     * @param p_path path or null / empty
     * @return default path on empty or input path
     */
    private static String orDEfaultPath( final String p_path )
    {
        return ( p_path == null ) || ( p_path.isEmpty() )
               ? Stream.of(
                    System.getProperty( "user.home" ),
                    ".asimov",
                    "configuration.yaml"
                ).collect( Collectors.joining( File.separator ) )
                 : p_path;
    }

    /**
     * creates the default configuration
     *
     * @return full path
     * @throws IOException on any io error
     */
    public static String createdefault() throws IOException
    {
        final String l_path = Stream.of(
            System.getProperty( "user.home" ),
            ".asimov"
        ).collect( Collectors.joining( File.separator ) );

        new File( l_path ).mkdirs();
        Files.copy(
            CConfiguration.class.getResourceAsStream(  "configuration.yaml"   ),
            FileSystems.getDefault().getPath( l_path + File.separator + "configuration.yaml" ),
            StandardCopyOption.REPLACE_EXISTING
        );

        return l_path;
    }


    /**
     * returns a configuration value
     *
     * @param p_path path of the element
     * @tparam T returning type
     * @return value
     */
    public <T> T get( final String... p_path )
    {
        return recursivedescent( m_configuration, p_path );
    }

    /**
     * recursive descent
     *
     * @param p_map configuration map
     * @param p_path path
     * @tparam T returning type parameter
     * @return value
     */
    @SuppressWarnings( "unchecked" )
    private static <T> T recursivedescent( final Map<String, ?> p_map, final String... p_path )
    {
        if ( ( p_path == null ) || ( p_path.length == 0 ) )
            throw new CSemanticException( "path need not to be empty" );

        final Object l_data = p_map.get( p_path[0].toLowerCase( Locale.ROOT ) );
        return ( p_path.length == 1 ) || ( l_data == null )
               ? (T) l_data
               : (T) recursivedescent( (Map<String, ?>) l_data, Arrays.copyOfRange( p_path, 1, p_path.length ) );
    }

}
