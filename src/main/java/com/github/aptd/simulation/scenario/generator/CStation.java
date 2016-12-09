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

package com.github.aptd.simulation.scenario.generator;

import com.github.aptd.simulation.elements.error.CSemanticException;
import com.github.aptd.simulation.elements.graph.network.INetworkNode;
import com.github.aptd.simulation.ui.CHTTPServer;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.score.IAggregation;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;


/**
 * generator for full-qualified stations
 * s
 * @tparam T node identifier
 */
public final class CStation<T, G extends INetworkNode<T>> extends IBaseAgentGenerator<INetworkNode<T>>
{
    /**
     * ctor reference
     */
    private final Constructor<G> m_ctor;

    /**
     * ctor
     *
     * @param p_stream asl input stream
     * @param p_classgenerator generator class
     * @throws Exception thrown on any parsing exception
     */
    public CStation( final InputStream p_stream, final Class<G> p_classgenerator ) throws Exception
    {
        super( p_stream, CCommon.actionsFromPackage().collect( Collectors.toSet() ), IAggregation.EMPTY );
        m_ctor = p_classgenerator.getConstructor( IAgentConfiguration.class, Object.class, double.class, double.class );
    }

    @Override
    public final INetworkNode<T> generatesingle( final Object... p_data )
    {
        try
        {
            final INetworkNode<T> l_agent = m_ctor.newInstance( m_configuration, p_data[0], p_data[1], p_data[2] );

            // add parameters to beliefbase
            l_agent.beliefbase().add( CLiteral.from( "name", CRawTerm.from( p_data[0].toString() ) ) );
            l_agent.beliefbase().add( CLiteral.from( "gps",
                                                    CLiteral.from( "longitude", CRawTerm.from( p_data[1] ) ),
                                                    CLiteral.from( "latitude", CRawTerm.from( p_data[2] ) )
            ) );

            // register agent at the restful API
            CHTTPServer.register( p_data[0].toString(), l_agent, "station" );
            return l_agent;
        }
        catch ( final IllegalAccessException | InvocationTargetException | InstantiationException l_exception )
        {
            throw new CSemanticException( l_exception );
        }
    }
}
