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
public final class CInspector implements IInspector
{
    private final CAgent m_node = new CAgent();

    @Override
    public final void inspectsleeping( final long p_value )
    {
        m_node.sleeping = p_value;
    }

    @Override
    public final void inspectcycle( final long p_value )
    {
        m_node.sleeping = p_value;
    }

    @Override
    public final void inspectbelief( final Stream<ILiteral> p_value )
    {

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

    }

    @Override
    public final void inspectstorage( final Stream<? extends Map.Entry<String, ?>> p_value )
    {

    }

    public final CAgent get()
    {
        return m_node;
    }
}
