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

import com.github.aptd.simulation.core.statistic.IStatistic;

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
     * returns a stream of all executable objects
     *
     * @return callable stream
     */
    Stream<Callable<?>> objects();

    /**
     * maximum simulation steps
     *
     * @return simulation steps
     */
    long simulationsteps();

    /**
     * returns simulation statistics
     *
     * @return statistic stream
     */
    Stream<IStatistic> statistic();

    /**
     * execute agents in parallel
     *
     * @return boolean flag for parallel execution
     */
    boolean parallel();

}

