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

package com.github.aptd.simulation.error;

import java.text.MessageFormat;


/**
 * exception of unmodifiable access
 */
@SuppressWarnings( "serial" )
public class CUnmodifiableException extends RuntimeException
{

    /**
     * ctor
     */
    public CUnmodifiableException()
    {
        super();
    }

    /**
     * ctor
     *
     * @param p_message any message
     * @param p_values any object values which are printed
     */
    public CUnmodifiableException( final String p_message, final Object... p_values )
    {
        super( MessageFormat.format( p_message, p_values ) );
    }

    /**
     * ctor
     *
     * @param p_cause any throwable
     */
    public CUnmodifiableException( final Throwable p_cause )
    {
        super( p_cause );
    }

}
