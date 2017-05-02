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

package com.github.aptd.simulation.core.experiment.local;

import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.statistic.IStatistic;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Stream;


/**
 * experiment definition
 */
public final class CExperiment implements IExperiment
{
    /**
     * simulation steps
     */
    private final long m_steps;
    /**
     * parallel execution
     */
    private final boolean m_parallel;
    /**
     * statistic object
     */
    private final Set<IStatistic> m_statistic;
    /**
     * agents
     */
    private final Map<String, Callable<?>> m_agents;


    /**
     * ctor
     *
     * @param p_steps number of simulation steps
     * @param p_parallel parallel object execution
     * @param p_statistic statistic objects
     * @param p_agents agents
     */
    public CExperiment( final long p_steps, final boolean p_parallel, final Set<IStatistic> p_statistic,
                        final Map<String, Callable<?>> p_agents
    )
    {
        m_steps = p_steps;
        m_parallel = p_parallel;
        m_statistic = p_statistic;
        m_agents = p_agents;
    }


    @Override
    public final Stream<Callable<?>> objects()
    {
        return m_agents.values().stream();
    }

    @Override
    public final long simulationsteps()
    {
        return m_steps;
    }

    @Override
    public final Stream<IStatistic> statistic()
    {
        return m_statistic.stream();
    }

    @Override
    public final boolean parallel()
    {
        return m_parallel;
    }
}
