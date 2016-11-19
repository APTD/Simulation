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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;


/**
 * class for any helper calls
 */
public final class CCommon
{
    /**
     * package name
     **/
    public static final String PACKAGEROOT = "com.github.aptd.simulation";
    /**
     * logger
     */
    private static final Logger LOGGER = CCommon.logger( CCommon.class );
    /**
     * language resource bundle
     **/
    private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle(
        MessageFormat.format( "{0}.{1}", PACKAGEROOT, "language" ),
        Locale.getDefault(),
        new CUTF8Control()
    );
    /**
     * properties of the package
     */
    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle(
        MessageFormat.format( "{0}.{1}", PACKAGEROOT, "configuration" ),
        Locale.getDefault(),
        new CUTF8Control()
    );


    /**
     * private ctor - avoid instantiation
     */
    private CCommon()
    {
    }

    /**
     * returns a logger instance
     *
     * @param p_class class type
     * @return logger
     */
    public static Logger logger( final Class<?> p_class )
    {
        return Logger.getLogger( p_class.getName() );
    }

    /**
     * list of usable languages
     *
     * @return list of language pattern
     */
    public static String[] languages()
    {
        return Arrays.stream( PROPERTIES.getString( "translation" ).split( "," ) ).map( i -> i.trim().toLowerCase() ).toArray( String[]::new );
    }

    /**
     * returns the language bundle
     *
     * @return bundle
     */
    public static ResourceBundle languagebundle()
    {
        return LANGUAGE;
    }

    /**
     * returns the property data of the package
     *
     * @return bundle object
     */
    public static ResourceBundle configuration()
    {
        return PROPERTIES;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * concats an URL with a path
     *
     * @param p_base base URL
     * @param p_string additional path
     * @return new URL
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    public static URL concaturl( final URL p_base, final String p_string ) throws MalformedURLException, URISyntaxException
    {
        return new URL( p_base.toString() + p_string ).toURI().normalize().toURL();
    }

    /**
     * returns root path of the resource
     *
     * @return URL of file or null
     */
    public static URL resourceurl()
    {
        return org.lightjason.agentspeak.common.CCommon.class.getClassLoader().getResource( "" );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file
     * @return URL of file or null
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    public static URL resourceurl( final String p_file ) throws URISyntaxException, MalformedURLException
    {
        return resourceurl( new File( p_file ) );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file relative to the CMain
     * @return URL of file or null
     *
     * @throws URISyntaxException is thrown on URI errors
     * @throws MalformedURLException is thrown on malformat
     */
    private static URL resourceurl( final File p_file ) throws URISyntaxException, MalformedURLException
    {
        if ( p_file.exists() )
            return p_file.toURI().normalize().toURL();
        return org.lightjason.agentspeak.common.CCommon.class.getClassLoader().getResource( p_file.toString().replace( File.separator, "/" ) ).toURI()
                                                             .normalize().toURL();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * returns the language depend string on any object
     *
     * @param p_source any object
     * @param p_label label name
     * @param p_parameter parameter
     * @return translated string
     *
     * @tparam T object type
     */
    public static <T> String languagestring( final T p_source, final String p_label, final Object... p_parameter )
    {
        return languagestring( p_source.getClass(), p_label, p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String languagestring( final Class<?> p_class, final String p_label, final Object... p_parameter )
    {
        try
        {
            return MessageFormat.format( LANGUAGE.getString( languagelabel( p_class, p_label ) ), p_parameter );
        }
        catch ( final MissingResourceException l_exception )
        {
            return "";
        }
    }

    /**
     * returns the label of a class and string to get access to the resource
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @return label name
     */
    private static String languagelabel( final Class<?> p_class, final String p_label )
    {
        return ( p_class.getCanonicalName().toLowerCase( Locale.ROOT ) + "." + p_label.toLowerCase( Locale.ROOT ) ).replaceAll( "[^a-zA-Z0-9_\\.]+", "" )
                                                                                                                   .replace(
                                                                                                                       PACKAGEROOT + ".", "" );
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    private static final class CUTF8Control extends ResourceBundle.Control
    {

        public final ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader,
                                               final boolean p_reload
        ) throws IllegalAccessException, InstantiationException, IOException
        {
            final InputStream l_stream;
            final String l_resource = this.toResourceName( this.toBundleName( p_basename, p_locale ), "properties" );

            if ( !p_reload )
                l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null )
                    return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null )
                    return null;

                l_connection.setUseCaches( false );
                l_stream = l_connection.getInputStream();
            }

            final ResourceBundle l_bundle = new PropertyResourceBundle( new InputStreamReader( l_stream, "UTF-8" ) );
            l_stream.close();
            return l_bundle;
        }
    }

}
