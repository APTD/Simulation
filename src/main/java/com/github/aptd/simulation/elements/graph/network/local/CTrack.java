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

package com.github.aptd.simulation.elements.graph.network.local;

import com.github.aptd.simulation.core.environment.IEnvironment;
import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * network edge class to link to nodestrack of the network
 *
 * @todo add actions
 */
public final class CTrack extends IBaseElement<ITrack<?>> implements ITrack<ITrack<?>>
{
    /**
     * literal functor
     */
    private static final String FUNCTOR = "track";
    /**
     * current maximum speed
     */
    private final AtomicDouble m_maximumspeed = new AtomicDouble( Double.MAX_VALUE );
    /**
     * from station
     */
    private final IStation<?> m_from;
    /**
     * to station
     */
    private final IStation<?> m_to;

    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id track identifier
     * @param p_from from station
     * @param p_to target station
     * @param p_environment environment
     */
    private CTrack( final IAgentConfiguration<ITrack<?>> p_configuration, final String p_id, final IStation<?> p_from, final IStation<?> p_to,
                    final IEnvironment p_environment
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_environment );
        m_from = p_from;
        m_to = p_to;
    }

    @Override
    public final IStation<?> from()
    {
        return m_from;
    }

    @Override
    public final IStation<?> to()
    {
        return m_to;
    }


    @Override
    protected final Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return Stream.of(
            CLiteral.from( "maximumspeed", CRawTerm.from( m_maximumspeed.get() ) )
        );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<ITrack<?>>
    {

        /**
         * ctor
         *
         * @param p_stream stream
         * @param p_actions action
         * @param p_environment environment
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final IEnvironment p_environment ) throws Exception
        {
            super( p_stream, p_actions, CTrack.class, p_environment );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected final Pair<ITrack<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CTrack( m_configuration, p_data[0].toString(), (IStation<?>) p_data[1], (IStation<?>) p_data[2], m_environment ),
                Stream.of( FUNCTOR )
            );
        }
    }
}
