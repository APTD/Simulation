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

package com.github.aptd.simulation.scenario.model.train;

import com.github.aptd.simulation.scenario.model.passenger.IPassenger;
import com.google.common.collect.Sets;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import java.util.Set;
import java.util.stream.Stream;


/**
 * wagon class
 */
public final class CWagon implements IWagon
{
    /**
     * set with agents
     */
    private Set<IPassenger<?>> m_passanger = Sets.newConcurrentHashSet();
    /**
     * maximum passanger
     */
    private final int m_maximum;

    /**
     * passanger
     *
     * @param p_maximum maximum
     */
    public CWagon( final int p_maximum )
    {
        m_maximum = p_maximum;
    }

    @Override
    public IWagon announcement( final ITrigger p_trigger )
    {
        m_passanger.parallelStream().forEach( i -> i.trigger( p_trigger ) );
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
    public final IWagon add( final IPassenger<?> p_passenger )
    {
        return null;
    }

    @Override
    public final IWagon remove( final IPassenger<?> p_passenger )
    {
        return null;
    }

    @Override
    public final Stream<IPassenger<?>> stream()
    {
        return null;
    }
}
