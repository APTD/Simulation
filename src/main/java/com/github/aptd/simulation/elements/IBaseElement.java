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

package com.github.aptd.simulation.elements;

import com.github.aptd.simulation.ui.CHTTPServer;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.agent.fuzzy.IFuzzy;
import org.lightjason.agentspeak.beliefbase.CBeliefbasePersistent;
import org.lightjason.agentspeak.beliefbase.storage.CMultiStorage;
import org.lightjason.agentspeak.beliefbase.storage.CSingleStorage;
import org.lightjason.agentspeak.beliefbase.view.IView;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.CDefaultAgentConfiguration;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.IShallowCopy;
import org.lightjason.agentspeak.language.execution.IVariableBuilder;
import org.lightjason.agentspeak.language.execution.action.unify.IUnifier;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * base simulation element structure
 */
public abstract class IBaseElement<N extends IElement<?>> extends IBaseAgent<N> implements IElement<N>
{
    /**
     * agent name
     */
    protected final String m_id;
    /**
     * functor definition
     */
    private final String m_functor;
    /**
     * reference to external beliefbase
     */
    private final IView<N> m_external;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     */
    protected IBaseElement( final IAgentConfiguration<N> p_configuration, final String p_functor, final String p_id )
    {
        super( p_configuration );
        m_id = p_id;
        m_functor = p_functor;

        m_external = m_beliefbase.beliefbase().view( "extern" );
    }

    @Override
    public final String id()
    {
        return m_id;
    }

    @Override
    public final int hashCode()
    {
        return m_id.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IElement<?> ) && ( p_object.hashCode() == this.hashCode() );
    }

    @Override
    public final Stream<ILiteral> literal( final IElement<?>... p_object )
    {
        return this.literal( Arrays.stream( p_object ) );
    }

    @Override
    public final Stream<ILiteral> literal( final Stream<IElement<?>> p_object )
    {
        return Stream.of(
            CLiteral.from(
                m_functor,
                Stream.concat(
                    Stream.concat(
                        Stream.of(
                            CLiteral.from( "id", CRawTerm.from( m_id ) )
                        ),
                        m_external.stream().map( IShallowCopy::shallowcopysuffix ).sorted().sequential()
                    ),
                    this.individualliteral( p_object ).sorted().sequential()
                )
            )
        );
    }

    /**
     * define object literal addons
     *
     * @param p_object calling objects
     * @return literal stream
     */
    protected abstract Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object );


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * base agent generator
     *
     * @tparam N agent type
     */
    protected abstract static class IBaseGenerator<N extends IElement<?>> extends IBaseAgentGenerator<N> implements IGenerator<N>
    {

        /**
         * ctor
         *
         * @param p_stream stream
         * @param p_actions action
         * @param p_agentclass agent class with internal actions
         * @throws Exception on any error
         */
        protected IBaseGenerator( final InputStream p_stream, final Set<IAction> p_actions,
                                  final Class<? extends N> p_agentclass ) throws Exception
        {
            super( p_stream, Stream.concat( p_actions.stream(), CCommon.actionsFromAgentClass( p_agentclass ) ).collect( Collectors.toSet() ) );
        }

        @Override
        public final N generatesingle( final Object... p_data )
        {
            return CHTTPServer.register( this.generate( p_data ) );
        }

        /**
         * generates the agent
         *
         * @param p_data creating arguments
         * @return agent object and group names
         */
        protected abstract Pair<N, Stream<String>> generate( final Object... p_data );

        @Override
        public IGenerator<N> resetcount()
        {
            return this;
        }

        @Override
        protected IAgentConfiguration<N> configuration( final IFuzzy<Boolean, N> p_fuzzy, final Collection<ILiteral> p_initalbeliefs, final Set<IPlan> p_plans,
                                                        final Set<IRule> p_rules,
                                                        final ILiteral p_initialgoal, final IUnifier p_unifier,
                                                        final IVariableBuilder p_variablebuilder
        )
        {
            return new CConfiguration( p_fuzzy, p_initalbeliefs, p_plans, p_rules, p_initialgoal, p_unifier, p_variablebuilder );
        }

        /**
         * agent configuration
         */
        private final class CConfiguration extends CDefaultAgentConfiguration<N>
        {
            public CConfiguration( final IFuzzy<Boolean, N> p_fuzzy, final Collection<ILiteral> p_initalbeliefs, final Set<IPlan> p_plans, final Set<IRule> p_rules,
                                   final ILiteral p_initialgoal,
                                   final IUnifier p_unifier, final IVariableBuilder p_variablebuilder
            )
            {
                super( p_fuzzy, p_initalbeliefs, p_plans, p_rules, p_initialgoal, p_unifier, p_variablebuilder );
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public final IView<N> beliefbase()
            {
                final IView<N> l_view = new CBeliefbasePersistent<N>( new CMultiStorage<>() ).create( BELIEFBASEROOTNAME );
                l_view.add( new CBeliefbasePersistent<N>( new CSingleStorage<ILiteral, IView<N>, N>() ).create( "extern", l_view ) );

                // add initial beliefs and clear initial beliefbase trigger
                m_initialbeliefs.parallelStream().forEach( i -> l_view.add( i.shallowcopy() ) );
                l_view.trigger();

                return l_view;
            }
        }
    }

}
