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

package com.github.aptd.simulation.core.experiment;

import com.github.aptd.simulation.core.writer.IWriter;
import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.action.IAction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.stream.Stream;


/**
 * a single experiment run
 *
 * @bug incomplete
 */
public interface IExperiment
{
    /**
     * empty experiment
     */
    IExperiment EMPTY = new CEmptyExperiment();

    /**
     * returns a stream of all executable objects
     *
     * @return callable stream
     */
    @Nonnull
    Stream<IElement<?>> objects();

    /**
     * preprocessing objects
     *
     * @return object stream
     */
    @Nonnull
    Stream<Callable<?>> preprocess();

    /**
     * maximum simulation steps
     *
     * @return simulation steps
     */
    @Nonnegative
    long simulationsteps();

    /**
     * statistic writer
     *
     * @return self-reference
     */
    @Nonnull
    IExperiment statistic( @Nonnull final IWriter p_writer );

    /**
     * returns internal experiment actions
     *
     * @return action stream
     */
    @Nonnull
    Stream<IAction> actions();

    /**
     * execute agents in parallel
     *
     * @return boolean flag for parallel execution
     */
    boolean parallel();

    /**
     * empty experiment
     */
    class CEmptyExperiment implements IExperiment
    {
        @Nonnull
        @Override
        public final Stream<IElement<?>> objects()
        {
            return Stream.empty();
        }

        @Nonnull
        @Override
        public final Stream<Callable<?>> preprocess()
        {
            return Stream.empty();
        }

        @Nonnegative
        @Override
        public final long simulationsteps()
        {
            return 0;
        }

        @Nonnull
        @Override
        public final IExperiment statistic( @Nonnull final IWriter p_writer )
        {
            return this;
        }

        @Nonnull
        @Override
        public final Stream<IAction> actions()
        {
            return Stream.empty();
        }

        @Override
        public final boolean parallel()
        {
            return false;
        }
    }


}

