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

package com.github.aptd.simulation.factory;

/**
 * factory for building simulation entities
 *
 * @bug incomplete
 */
public enum EFactory
{
    LOCAL( new CLocal() );


    /**
     * factory object
     */
    private final IFactory m_factory;

    /**
     * ctor
     *
     * @param p_factory factory instance
     */
    EFactory( final IFactory p_factory )
    {
        m_factory = p_factory;
    }

    /**
     * returns the factory
     *
     * @return factory instance
     */
    public final IFactory factory()
    {
        return m_factory;
    }

    /**
     * factory
     *
     * @param p_value string value
     * @return factory object
     */
    public static EFactory from( final String p_value )
    {
        return EFactory.valueOf( p_value.trim().toUpperCase() );
    }
}
