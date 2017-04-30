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

package com.github.aptd.simulation.elements.linearprogram;

import java.util.stream.Stream;


/**
 * interface of a linear program
 *
 * @bug incompelet
 */
public interface ILinearProgram
{
    /**
     * returns a stream of variables and variable-type
     *
     * @return stream of variable and variable-type
     */
    Stream<IVariable> variable();

    /**
     * interface of a LP variable
     */
    interface IVariable
    {

        /**
         * variable name
         *
         * @return name
         */
        String name();

        /**
         * upper-bound of the variable
         *
         * @return number
         */
        Number upperbound();

        /**
         * lower-bound of the variable
         *
         * @return number
         */
        Number lowerbound();

        /**
         * variable type
         *
         * @return number type
         */
        EType type();

    }

    /**
     * soling type
     */
    enum EType
    {
        BOOLEAN,
        INTEGER,
        REAL;

        /**
         * converts the enum type to the
         * solver specific type
         *
         * @param p_solver solver instance
         * @tparam T solver specific type
         * @return solver specific type
         */
        public <T> T specific( final ISolver<T> p_solver )
        {
            return p_solver.typespecific( this );
        }
    }
}
