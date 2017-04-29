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
import com.github.aptd.simulation.error.CSemanticException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * train class
 */
public final class CTrain extends IBaseElement<ITrain<?>> implements ITrain<ITrain<?>>
{
    /**
     * literal functor
     */
    private static final String FUNCTOR = "train";

    /**
     * list of wagons
     * @todo change to map with wagon names and ordering
     */
    private final List<IWagon<?>> m_wagon;


    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id train identifier
     * @param p_wagon wagon references
     */
    private CTrain( final IAgentConfiguration<ITrain<?>> p_configuration, final String p_id, final Stream<IWagon<?>> p_wagon )
    {
        super( p_configuration, p_id, FUNCTOR );
        m_wagon = p_wagon.collect( Collectors.toList() );
    }

    @Override
    public final int wagon()
    {
        return m_wagon.size();
    }

    @Override
    public final ITrain<ITrain<?>> addwagon( final IWagon<?> p_wagon )
    {
        m_wagon.add( p_wagon );
        return this;
    }

    @Override
    public final IWagon<?> removewagon()
    {
        if ( m_wagon.size() == 1 )
            throw new CSemanticException( "train [{0}] cannot be empty", m_id );

        return m_wagon.remove( m_wagon.size() - 1 );
    }


    @Override
    protected Stream<ILiteral> individualliteral( final Stream<com.github.aptd.simulation.elements.IElement<?>> p_object )
    {
        return Stream.of(
            CLiteral.from( "wagon", CRawTerm.from( this.wagon() ) )
        );
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<ITrain<?>>
    {

        /**
         * @param p_stream stream
         * @param p_actions action
         * @param p_aggregation aggregation
         * @throws Exception on any error
         */
        protected CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final IAggregation p_aggregation ) throws Exception
        {
            super( p_stream, p_actions, p_aggregation, CTrain.class );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected final Pair<ITrain<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                new CTrain(
                    m_configuration,
                    p_data[0].toString(),
                    Arrays.stream( p_data ).skip( 1 ).map( i -> (IWagon<?>) i )
                ),
                Stream.of( FUNCTOR )
            );
        }
    }

}
