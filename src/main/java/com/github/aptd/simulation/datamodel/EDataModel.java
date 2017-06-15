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

import java.util.Locale;


/**
 * datamodel factory
 */
public enum EDataModel
{
    XML( new CXMLReader() );

    /**
     * model instance
     */
    private final IDataModel m_model;

    /**
     * ctor
     *
     * @param p_model data-model
     */
    EDataModel( final IDataModel p_model )
    {
        m_model = p_model;
    }

    /**
     * get data-model
     * @return data-model instance
     */
    public final IDataModel model()
    {
        return m_model;
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
