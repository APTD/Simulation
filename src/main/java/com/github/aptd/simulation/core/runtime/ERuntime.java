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

package com.github.aptd.simulation.core.runtime;

import com.github.aptd.simulation.common.CCommon;
import com.github.aptd.simulation.core.runtime.local.CRuntime;
import com.github.aptd.simulation.error.CNotFoundException;

import java.util.Locale;


/**
 * runtime factory
 */
public enum  ERuntime
{
    LOCAL;

    /**
     * creates a new runtime instance
     *
     * @return runtime instance
     */
    public final IRuntime get()
    {
        switch ( this )
        {
            case LOCAL: return new CRuntime();

            default:
                throw new CNotFoundException( CCommon.languagestring( this, "notfound", this ) );
        }
    }

    /**
     * factory
     *
     * @param p_value runtime name
     * @return runtime
     */
    public static ERuntime from( final String p_value )
    {
        return ERuntime.valueOf( p_value.trim().toUpperCase( Locale.ROOT ) );
    }

}
