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

import com.github.aptd.simulation.common.CAgentTrigger;
import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.statistic.IStatistic;
import com.github.aptd.simulation.core.writer.IWriter;
import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
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
    private final Map<String, IElement<?>> m_agents = new HashMap<>();


    /**
     * ctor
     *
     * @param p_steps number of simulation steps
     * @param p_parallel parallel object execution
     * @param p_statistic statistic objects
     */
    public CExperiment( final long p_steps, final boolean p_parallel, final Set<IStatistic> p_statistic )
    {
        m_steps = p_steps;
        m_parallel = p_parallel;
        m_statistic = p_statistic;
    }


    @Override
    public final Stream<Callable<?>> objects()
    {
        return Stream.of();
    }

    @Override
    public final long simulationsteps()
    {
        return m_steps;
    }

    @Override
    public final IExperiment statistic( final IWriter p_writer )
    {
        m_statistic.forEach( i -> i.write( p_writer ) );
        return this;
    }

    @Override
    public final boolean parallel()
    {
        return m_parallel;
    }


    /**
     * agent send action for agent communication
     */
    private final class CMessageSendAction extends IBaseAction
    {

        @Override
        public final IPath name()
        {
            return CPath.from( "asimov/send" );
        }

        @Override
        public final int minimalArgumentNumber()
        {
            return 1;
        }

        @Override
        public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument,
                                                   final List<ITerm> p_return, final List<ITerm> p_annotation )
        {
            final List<ITerm> l_arguments = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );
            if ( l_arguments.size() < 2 )
                return CFuzzyValue.from( false );

            final IAgent<?> l_receiver = m_agents.get( l_arguments.get( 0 ).<String>raw() );
            if ( l_receiver == null )
                return CFuzzyValue.from( false );

            final ILiteral l_sender = CAgentTrigger.messagersender( p_context.agent() );
            l_arguments.stream()
                       .skip( 1 )
                       .map( ITerm::raw )
                       .map( i -> CAgentTrigger.messagesend( i, l_sender ) )
                       .forEach( i -> l_receiver.trigger( i ) );

            return CFuzzyValue.from( true );
        }

    }

}
