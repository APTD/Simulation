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

package com.github.aptd.simulation.datamodel;

import com.github.aptd.simulation.common.CCommon;
import com.github.aptd.simulation.error.CNotFoundException;
import com.github.aptd.simulation.error.CRuntimeException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;


/**
 * datamodel factory
 */
public enum EDataModel
{
    XML;

    /**
     * get data-model from file
     *
     * @param p_file file path
     * @return data-model
     */
    public final IDataModel get( final String p_file )
    {
        try
        (
            final InputStream l_stream = new FileInputStream( p_file );
        )
        {

            switch ( this )
            {
                case XML: return CXMLReader.from( l_stream );

                default:
                    throw new CNotFoundException( CCommon.languagestring( this, "readernotfound", this ) );
            }
        }
        catch ( final Exception l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
    }

    /**
     * factory
     *
     * @param p_value data-model name
     * @return data-model
     */
    public static EDataModel from( final String p_value )
    {
        return EDataModel.valueOf( p_value.trim().toUpperCase( Locale.ROOT ) );
    }
}
