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

package com.github.aptd.simulation.core.environment;

import com.github.aptd.simulation.core.environment.local.CEnvironment;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * enum of environments
 */
public enum EEnvironment
{
    LOCAL;

    /**
     * generates an environment instance
     *
     * @return a new environment instance
     */
    public final IEnvironment generate()
    {
        switch ( this )
        {
            case LOCAL : return new CEnvironment();

            default :
                throw new RuntimeException( MessageFormat.format( "environment [{0}] not exists", this ) );
        }
    }

    /**
     * returns a environment enum environment
     * case-insensitive ignore string name
     *
     * @param p_name string name
     * @return enironment enum
     */
    public static EEnvironment from( final String p_name )
    {
        return EEnvironment.valueOf(  p_name.toUpperCase( Locale.ROOT ) );
    }

    /**
     * returns a string with a
     * comma-separated list of enum elements
     *
     * @return string list
     */
    public static String list()
    {
        return Arrays.stream( EEnvironment.values() )
                     .map( i -> i.name() )
                     .map( i -> i.toLowerCase( Locale.ROOT ) )
                     .collect( Collectors.joining( ", " ) );
    }
}
