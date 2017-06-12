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

import com.github.aptd.simulation.core.environment.IEnvironment;
import com.github.aptd.simulation.core.writer.IWriter;
import com.github.aptd.simulation.elements.IElement;

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
    Stream<IElement<?>> objects();

    /**
     * maximum simulation steps
     *
     * @return simulation steps
     */
    long simulationsteps();

    /**
     * returns the environment
     *
     * @return environment
     */
    IEnvironment environment();

    /**
     * statistic writer
     *
     * @return self-reference
     */
    IExperiment statistic( final IWriter p_writer );

    /**
     * execute agents in parallel
     *
     * @return boolean flag for parallel execution
     */
    boolean parallel();

}

