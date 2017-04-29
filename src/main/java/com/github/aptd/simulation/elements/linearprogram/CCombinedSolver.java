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

import com.github.aptd.simulation.error.CNotFoundException;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpVariable;

import java.text.MessageFormat;


/**
 * LP solver component
 *
 * @todo messageformat convert to language behaviour
 * @see http://jmpi.sourceforge.net/
 */
public final class CCombinedSolver implements ISolver<MpVariable.Type>
{

    @Override
    public final ISolver<MpVariable.Type> solve( final ILinearProgram p_lp )
    {
        final MpProblem l_problem = new MpProblem();

        p_lp.variable()
            .map( i -> new MpVariable( i.name(), i.lowerbound(), i.upperbound(), i.type().specific( this ) ) )
            .forEach( l_problem::addVariable );

        return this;
    }

    @Override
    public final MpVariable.Type typespecific( final ILinearProgram.EType p_type )
    {
        switch ( p_type )
        {
            case BOOLEAN: return MpVariable.Type.BOOL;

            case INTEGER: return MpVariable.Type.INT;

            case REAL: return MpVariable.Type.REAL;

            default:
                throw new CNotFoundException( MessageFormat.format( "variable type [{0}] not known", p_type ) );
        }
    }

}
