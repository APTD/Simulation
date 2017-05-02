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

package com.github.aptd.simulation.core.writer;

/**
 * writer interface to export statistic
 */
public interface IWriter
{

    /**
     * add a new section to the out put
     *
     * @param p_depth depth of the section (zero is root)
     * @param p_description any description
     * @return self-reference
     */
    IWriter section( final int p_depth, final String p_description );

    /**
     * adds a single value
     *
     * @param p_description any description
     * @param p_value value
     * @tparam T value type
     * @return self-reference
     */
    <T> IWriter value( final String p_description, final T p_value );


}
