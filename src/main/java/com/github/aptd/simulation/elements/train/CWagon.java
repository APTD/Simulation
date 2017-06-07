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

package com.github.aptd.simulation.elements.train;

import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * wagon class
 */
public final class CWagon extends IBaseElement<IWagon<?>> implements IWagon<IWagon<?>>
{
    /**
     * literal functor
     */
    private static final String FUNCTOR = "wagon";

    /**
     * set with agents
     */
    private Set<IPassenger<?>> m_passanger = Sets.newConcurrentHashSet();
    /**
     * maximum passanger
     */
    private final int m_maximum;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id identifier of the wagon
     */
    private CWagon( final IAgentConfiguration<IWagon<?>> p_configuration, final String p_id, final int p_maximum )
    {
        super( p_configuration, FUNCTOR, p_id );
        m_maximum = p_maximum;
    }

    @Override
    public final IWagon<IWagon<?>> announcement( final ITrigger p_trigger )
    {
        m_passanger.parallelStream().forEach( i -> i.trigger( p_trigger ) );
        return this;
    }

    @Override
    public IWagon<IWagon<?>> add( final IPassenger<?> p_passenger )
    {
        m_passanger.add( p_passenger );
        return this;
    }

    @Override
    public IWagon<IWagon<?>> remove( final IPassenger<?> p_passenger )
    {
        m_passanger.remove( p_passenger );
        return this;
    }

    @Override
    public int free()
    {
        return m_maximum - m_passanger.size();
    }

    @Override
    public int size()
    {
        return m_passanger.size();
    }

    @Override
    public final Stream<IPassenger<?>> passenger()
    {
        return m_passanger.stream();
    }

    @Override
    protected final Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return Stream.of(
            CLiteral.from( "free", CRawTerm.from( this.free() ) )
        );
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IWagon<?>>
    {

        /**
         * @param p_stream stream
         * @param p_actions action
         * @throws Exception on any error
         */
        protected CGenerator( final InputStream p_stream, final Set<IAction> p_actions ) throws Exception
        {
            super( p_stream, p_actions, CWagon.class );
        }

        @Override
        protected final Pair<IWagon<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CWagon( m_configuration, p_data[0].toString(), (int) p_data[1] ),
                Stream.of( FUNCTOR )
            );
        }
    }


}
