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

package com.github.aptd.simulation.ui.agent;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lightjason.agentspeak.agent.IInspector;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;

import java.util.Map;
import java.util.stream.Stream;


/**
 * inspector of an agent
 */
public final class CReSTInspector implements IInspector, IReSTInspector
{
    private final CReSTAgent<String> m_node = new CReSTAgent<>();

    /**
     * ctor
     *
     * @param p_id agent id
     */
    public CReSTInspector( final String p_id )
    {
        m_node.setID( p_id );
    }

    @Override
    public final void inspectsleeping( final long p_value )
    {
        m_node.setSleeping( p_value );
    }

    @Override
    public final void inspectcycle( final long p_value )
    {
        m_node.setCycle( p_value );
    }

    @Override
    public final void inspectbelief( final Stream<ILiteral> p_value )
    {
        p_value.forEach( i -> m_node.setBelief( i.toString() ) );
    }

    @Override
    public final void inspectplans( final Stream<ImmutableTriple<IPlan, Long, Long>> p_value
    )
    {
    }

    @Override
    public final void inspectrules( final Stream<IRule> p_value )
    {
    }

    @Override
    public final void inspectrunningplans( final Stream<ILiteral> p_value )
    {
        p_value.forEach( i -> m_node.setRunningplan( i.toString() ) );
    }

    @Override
    public final void inspectstorage( final Stream<? extends Map.Entry<String, ?>> p_value )
    {
        p_value.forEach( i -> m_node.setStorage( i ) );
    }

    @Override
    public final IReSTAgent get()
    {
        return m_node;
    }
}
