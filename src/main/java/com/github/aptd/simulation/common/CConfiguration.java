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

import com.github.aptd.simulation.error.CSemanticException;
import org.lightjason.agentspeak.action.IAction;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
     * all global agent actions
     */
    private final Set<IAction> m_agentaction = org.lightjason.agentspeak.common.CCommon.actionsFromPackage().collect( Collectors.toSet() );

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
    public final CConfiguration loadfile( final String p_path )
    {

        try
            (
                final InputStream l_stream = new FileInputStream( orDefaultPath( p_path ) );
            )
        {

            final Map<String, ?> l_result = (Map<String, Object>) new Yaml().load( l_stream );
            if ( l_result != null )
            {
                m_configuration.clear();
                m_configuration.putAll( l_result );
            }

        }
        catch ( final IOException l_exception )
        {
            throw new CSemanticException( l_exception );
        }

        return this;
    }

    /**
     * loads the configuration from a string
     *
     * @param p_yaml yaml string
     * @return self reference
     */
    @SuppressWarnings( "unchecked" )
    public final CConfiguration loadstring( final String p_yaml )
    {
        final Map<String, ?> l_result = (Map<String, Object>) new Yaml().load( p_yaml );
        if ( l_result != null )
        {
            m_configuration.clear();
            m_configuration.putAll( l_result );
        }
        return this;
    }

    /**
     * set default path
     *
     * @param p_path path or null / empty
     * @return default path on empty or input path
     */
    private static String orDefaultPath( final String p_path )
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
     * returns a set with all agent actions
     *
     * @param p_action arguments with additional actions
     * @return action set
     */
    public final Set<IAction> agentaction( final IAction... p_action )
    {
        return ( p_action == null ) || ( p_action.length == 0 )
               ? m_agentaction
               : this.agentaction( Arrays.stream( p_action ) );
    }

    /**
     * returns a set with all agent actions
     *
     * @param p_action stream with additional actions
     * @return action set
     */
    public final Set<IAction> agentaction( final Stream<IAction> p_action )
    {
        return Stream.concat( m_agentaction.stream(), p_action ).collect( Collectors.toSet() );
    }

    /**
     * returns a configuration value
     *
     * @param p_path path of the element
     * @tparam T returning type
     * @return value or null
     */
    public final <T> T get( final String... p_path )
    {
        return recursivedescent( m_configuration, p_path );
    }

    /**
     * returns a configuration value or on not
     * existing the default value
     *
     * @param p_default default value
     * @param p_path path of the element
     * @tparam T returning type
     * @return value / default vaue
     */
    public final <T> T getOrDefault( final T p_default, final String... p_path )
    {
        final T l_result = recursivedescent( m_configuration, p_path );
        return l_result == null
               ? p_default
               : l_result;
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
