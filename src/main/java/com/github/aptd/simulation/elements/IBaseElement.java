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

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.aptd.simulation.common.CAgentTrigger;
import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.ui.CHTTPServer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.beliefbase.CBeliefbase;
import org.lightjason.agentspeak.beliefbase.IBeliefbaseOnDemand;
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
import org.lightjason.agentspeak.language.execution.IVariableBuilder;
import org.lightjason.agentspeak.language.fuzzy.operator.IFuzzyBundle;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;
import org.lightjason.agentspeak.language.unify.IUnifier;
import org.pmw.tinylog.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * base simulation element structure
 */
@IAgentAction
public abstract class IBaseElement<N extends IElement<?>> extends IBaseAgent<N> implements IElement<N>
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 2131082093359879516L;
    /**
     * agent name
     */
    protected final String m_id;
    /**
     * reference to time
     */
    protected final ITime m_time;
    /**
     * simulated time at which the agent will become active again on its own
     */
    protected Instant m_nextactivation = Instant.MAX;
    /**
     * set of inputs to be processed
     */
    protected final ListMultimap<EMessageType, IMessage> m_input = Multimaps.synchronizedListMultimap( ArrayListMultimap.create() );
    /**
     * functor definition
     */
    private final String m_functor;
    /**
     * reference to external beliefbase
     */
    private final IView m_external;
    /**
     * timezone the agent lives in
     */
    private ZoneId m_timezone = ZoneId.systemDefault();
    /**
     * reference of the environment
     */
    private final AtomicReference<IEnvironment<N, ?>> m_environment = new AtomicReference<>();


    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_functor ...
     * @param p_id ...
     * @param p_time time reference
     */
    @SuppressWarnings( "unchecked" )
    protected IBaseElement( final IAgentConfiguration<N> p_configuration, final String p_functor, final String p_id, final ITime p_time )
    {
        super( p_configuration );
        m_id = p_id;
        m_functor = p_functor;

        m_time = p_time;
        m_external = m_beliefbase.beliefbase().view( "extern" );
        m_beliefbase.add( new CTimeBeliefbase().create( "time", m_beliefbase ) );
    }

    @Override
    public N call() throws Exception
    {
        if ( imminent() ) this.trigger( CAgentTrigger.ACTIVATE );
        return super.call();
    }

    /**
     * whether the element has a scheduled activation at the current time
     *
     * @return true if this component has a scheduled activation at the current time
     */
    @Override
    public boolean imminent()
    {
        return /* runningplans().isEmpty() && */ !m_nextactivation.isAfter( m_time.current() );
    }

    @Override
    public Instant nextactivation()
    {
        return m_nextactivation;
    }

    @JsonValue
    @Override
    public final String id()
    {
        return m_id;
    }

    @Override
    public final IElement<N> environment( final IEnvironment<N, ?> p_environment )
    {
        m_environment.set( p_environment );
        return this;
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
                        m_external.stream().map( i -> i.<ILiteral>shallowcopysuffix() ).sorted().sequential()
                    ),
                    this.individualliteral( p_object ).sorted().sequential()
                )
            )
        );
    }

    @Override
    public String toString()
    {
        return m_id;
    }

    /**
     * define object literal addons
     *
     * @param p_object calling objects
     * @return literal stream
     */
    protected abstract Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object );

    /**
     * get outgoing messages. empty by default
     *
     * @return Stream of messages
     */
    @Override
    public Stream<IMessage> output()
    {
        return Stream.of();
    }

    /**
     * store an incoming message to be processed
     *
     * @param p_message incoming message
     * @return self reference
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public N input( final IMessage p_message )
    {
        m_input.put( p_message.type(), p_message );
        m_nextactivation = m_time.current();
        return (N) this;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    @IAgentActionFilter
    @IAgentActionName( name = "simtime/current" )
    private ZonedDateTime currentTime()
    {
        return m_time.current().atZone( m_timezone );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "simtime/max" )
    private ZonedDateTime maxTime()
    {
        return ZonedDateTime.ofInstant( Instant.now().plus( Duration.ofDays( 9999 ) ), m_timezone );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "nextactivation/get" )
    private ZonedDateTime getNextActivation()
    {
        return m_nextactivation.atZone( m_timezone );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "nextactivation/set" )
    private void setNextActivation( @Nullable final ZonedDateTime p_datetime ) throws Exception
    {
        if ( p_datetime == null )
            throw new IllegalArgumentException( "next activation time must not be null" );

        final Instant l_instant = p_datetime.toInstant();
        if ( l_instant.compareTo( m_time.current() ) <= 0 )
            throw new IllegalArgumentException( "next activation time must be in the future" );

        m_nextactivation = l_instant;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * base agent generator
     *
     * @tparam N agent type
     */
    protected abstract static class IBaseGenerator<N extends IElement<?>> extends IBaseAgentGenerator<N> implements IGenerator<N>
    {
        /**
         * time reference
         */
        protected final ITime m_time;

        private JsonFactory m_factory;

        /**
         * ctor
         *
         * @param p_stream stream
         * @param p_actions action
         * @param p_agentclass agent class with internal actions
         * @param p_time time reference
         * @throws Exception on any error
         */
        protected IBaseGenerator( final InputStream p_stream, final Set<IAction> p_actions,
                                  final Class<? extends N> p_agentclass, final ITime p_time ) throws Exception
        {
            super( p_stream, Stream.concat( p_actions.stream(), CCommon.actionsFromAgentClass( p_agentclass ) ).collect( Collectors.toSet() ) );
            m_time = p_time;
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public final N generatesingle( final Object... p_data )
        {
            final Pair<N, Stream<String>> l_generate = this.generate( p_data );
            Logger.debug( () ->
            {
                String l_logstring = "";
                try
                {
                    if ( m_factory == null ) m_factory = new MappingJsonFactory();
                    final StringWriter l_writer = new StringWriter();
                    final JsonGenerator l_generator = m_factory.createGenerator( l_writer );
                    l_generator.writeStartObject();
                    l_generator.writeStringField( "type", "generation" );
                    l_generator.writeStringField( "id", l_generate.getLeft().id() );
                    if ( l_generate.getLeft() instanceof IStatefulElement<?> )
                    {
                        final IStatefulElement<N> l_element = (IStatefulElement<N>) l_generate.getLeft();
                        l_generator.writeObjectFieldStart( "state" );
                        l_element.writeState( l_generator );
                        l_generator.writeEndObject();
                    }
                    l_generator.writeEndObject();
                    l_writer.close();
                    l_generator.close();
                    l_logstring = l_writer.toString();
                }
                catch ( final IOException l_exception )
                {
                    l_exception.printStackTrace();
                }
                return l_logstring;
            } );
            return CHTTPServer.register( l_generate );
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
        protected IAgentConfiguration<N> configuration( @Nonnull final IFuzzyBundle<Boolean> p_fuzzy, @Nonnull final Collection<ILiteral> p_initalbeliefs,
                                                        @Nonnull final Set<IPlan> p_plans, @Nonnull final Set<IRule> p_rules,
                                                        @Nullable final ILiteral p_initialgoal,
                                                        @Nonnull final IUnifier p_unifier, @Nonnull final IVariableBuilder p_variablebuilder
        )
        {
            return new CConfiguration( p_fuzzy, p_initalbeliefs, p_plans, p_rules, p_initialgoal, p_unifier, p_variablebuilder );
        }

        /**
         * agent configuration
         */
        private final class CConfiguration extends CDefaultAgentConfiguration<N>
        {


            public CConfiguration( @Nonnull final IFuzzyBundle<Boolean> p_fuzzy,
                                   @Nonnull final Collection<ILiteral> p_initialbeliefs,
                                   @Nonnull final Set<IPlan> p_plans,
                                   @Nonnull final Set<IRule> p_rules,
                                   final ILiteral p_initialgoal,
                                   @Nonnull final IUnifier p_unifier,
                                   @Nonnull final IVariableBuilder p_variablebuilder
            )
            {
                super( p_fuzzy, p_initialbeliefs, p_plans, p_rules, p_initialgoal, p_unifier, p_variablebuilder );
            }

            @Nonnull
            @Override
            @SuppressWarnings( "unchecked" )
            public final IView beliefbase()
            {
                final IView l_view = new CBeliefbase( new CMultiStorage<>() ).create( BELIEFBASEROOTNAME );
                l_view.add( new CBeliefbase( new CSingleStorage<>() ).create( "extern", l_view ) );

                // add initial beliefs and clear initial beliefbase trigger
                m_initialbeliefs.parallelStream().forEach( i -> l_view.add( i.shallowcopy() ) );
                l_view.trigger();

                return l_view;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * on-demand beliefbase to get access to the time structure
     */
    private final class CTimeBeliefbase extends IBeliefbaseOnDemand<N>
    {

        @Nonnull
        @Override
        public final Stream<ILiteral> streamLiteral()
        {
            return m_time.literal( IBaseElement.this );
        }

        @Nonnull
        @Override
        public final Collection<ILiteral> literal( @Nonnull final String p_key )
        {
            return m_time.literal( IBaseElement.this )
                         .filter( i -> p_key.equals( i.functor() ) )
                         .collect( Collectors.toSet() );
        }

        @Override
        public final boolean empty()
        {
            return !m_time.literal( IBaseElement.this )
                          .findFirst()
                          .isPresent();
        }

        @Override
        public final int size()
        {
            return (int) m_time.literal( IBaseElement.this ).count();
        }

        @Override
        public final boolean containsLiteral( @Nonnull final String p_key )
        {
            return m_time.literal( IBaseElement.this )
                         .anyMatch( i -> p_key.equals( i.functor() ) );
        }

    }


}
