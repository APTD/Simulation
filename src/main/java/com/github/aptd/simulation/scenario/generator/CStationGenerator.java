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


import com.github.aptd.simulation.CHTTPServer;
import com.github.aptd.simulation.common.CConfiguration;
import com.github.aptd.simulation.error.CSemanticException;
import com.github.aptd.simulation.scenario.model.graph.network.INetworkNode;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * generator for full-qualified stations
 * s
 * @tparam T node identifier
 */
public final class CStationGenerator<T> extends IBaseAgentGenerator<INetworkNode<T>>
{

    /**
     * ctor reference
     */
    private final Constructor<? extends INetworkNode<T>> m_agentctor;


    /**
     * ctor
     *
     * @param p_stream ASL input stream
     * @param p_agentclass agent class for generation
     * @throws Exception is thrown on any error
     */
    public <G extends INetworkNode<T>> CStationGenerator( final InputStream p_stream, final Class<G> p_agentclass ) throws Exception
    {
        super( p_stream, CConfiguration.INSTANCE.agentaction(), CConfiguration.INSTANCE.agentaggregation() );
        m_agentctor = p_agentclass.getConstructor( IAgentConfiguration.class, Object.class, double.class, double.class );
    }

    @Override
    public final INetworkNode<T> generatesingle( final Object... p_data )
    {
        try
        {
            final INetworkNode<T> l_agent = m_agentctor.newInstance( m_configuration, p_data[0], p_data[1], p_data[2] );

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
