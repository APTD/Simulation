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
import com.github.aptd.simulation.core.environment.IEnvironment;
import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.statistic.IStatistic;
import com.github.aptd.simulation.core.writer.IWriter;
import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private final IStatistic m_statistic;
    /**
     * environment
     */
    private final IEnvironment m_environment;
    /**
     * agents
     */
    private final Map<String, IElement<?>> m_agents = new ConcurrentHashMap<>();
    /**
     * set with actions
     */
    private final Set<IAction> m_actions;


    /**
     * ctor
     *
     * @param p_steps number of simulation steps
     * @param p_parallel parallel object execution
     * @param p_statistic statistic object
     * @param p_agents stream with agents
     */
    public CExperiment( final long p_steps, final boolean p_parallel, @Nonnull final IStatistic p_statistic, @Nonnull final IEnvironment p_environment,
                        @Nonnull final Map<String, ? extends IElement<?>> p_agents )
    {
        m_steps = p_steps;
        m_parallel = p_parallel;
        m_statistic = p_statistic;
        m_environment = p_environment;
        m_agents.putAll( p_agents );

        m_actions = Collections.unmodifiableSet(
            Stream.concat(
                org.lightjason.agentspeak.common.CCommon.actionsFromPackage(),
                Stream.of( new CMessageSendAction() )
            ).collect( Collectors.toSet() )
        );
    }


    @Nonnull
    @Override
    public final Stream<IElement<?>> objects()
    {
        return  m_agents.values().stream();
    }

    @Nonnegative
    @Override
    public final long simulationsteps()
    {
        return m_steps;
    }

    @Nonnull
    @Override
    public IEnvironment environment()
    {
        return m_environment;
    }

    @Override
    public final IExperiment statistic( @Nonnull final IWriter p_writer )
    {
        m_statistic.write( p_writer );
        return this;
    }

    @Nonnull
    @Override
    public final Stream<IAction> actions()
    {
        return Stream.concat( m_actions.stream(), m_statistic.action() );
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

        @Nonnull
        @Override
        public final IPath name()
        {
            return CPath.from( "asimov/send" );
        }

        @Nonnegative
        @Override
        public final int minimalArgumentNumber()
        {
            return 1;
        }

        @Nonnull
        @Override
        public IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                             @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
        {
            final List<ITerm> l_arguments = CCommon.flatten( p_argument ).collect( Collectors.toList() );
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
