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

package com.github.aptd.simulation.core.runtime.local;


import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.runtime.IRuntime;

import java.util.concurrent.Callable;
import java.util.stream.LongStream;
import java.util.stream.Stream;


/**
 * local runtime
 * @todo refactor with logger
 */
public final class CRuntime implements IRuntime
{

    @Override
    public final IExperiment execute( final IExperiment p_experiment )
    {
        LongStream.range( 0, p_experiment.simulationsteps() )
                  .forEach( i -> optionalparallelstream( p_experiment.objects(), p_experiment.parallel() ).forEach( CRuntime::execute ) );

        return p_experiment;
    }

    /**
     * creates an optional parallel stream
     *
     * @param p_stream input stream
     * @return stream
     * @tparam T stream element type
     */
    private static <T> Stream<T> optionalparallelstream( final Stream<T> p_stream, final boolean p_parallel )
    {
        return p_parallel ? p_stream.parallel() : p_stream;
    }


    /**
     * execute callable object with catching exception
     *
     * @param p_object callable
     */
    private static void execute( final Callable<?> p_object )
    {
        try
        {
            p_object.call();
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
        }
    }

}
