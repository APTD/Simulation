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

package com.github.aptd.simulation.factory;


import com.github.aptd.simulation.core.environment.IEnvironment;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.IGraph;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.graph.network.local.CNetwork;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.elements.graph.network.local.CTransit;
import com.github.aptd.simulation.elements.graph.network.local.CVirtual;
import com.github.aptd.simulation.elements.train.CTrain;
import com.github.aptd.simulation.elements.train.ITrain;
import com.google.common.base.Function;
import org.lightjason.agentspeak.action.IAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * local factory
 */
public final class CLocal implements IFactory
{

    private IEnvironment m_environment;

    @Override
    public IFactory environment( @Nonnull final IEnvironment p_environment )
    {
        m_environment = p_environment;
        return this;
    }

    @Override
    public IEnvironment environment()
    {
        return m_environment;
    }

    @Nonnull
    @Override
    public IElement.IGenerator<ITrain<?>> train( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions ) throws Exception
    {
        return new CTrain.CGenerator( p_stream, p_actions, m_environment );
    }

    @Nonnull
    @Override
    public IGraph<IStation<?>, ITrack<?>> network( @Nonnull final Stream<ITrack<?>> p_edge, @Nullable final Function<ITrack<?>, ? extends Number>... p_weight )
    {
        return new CNetwork( p_edge, p_weight == null ? null : p_weight[0] );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> station( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions ) throws Exception
    {
        return new CStation.CGenerator( p_stream, p_actions, m_environment );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> transit( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions ) throws Exception
    {
        return new CTransit.CGenerator( p_stream, p_actions, m_environment );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> virtual( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions ) throws Exception
    {
        return new CVirtual.CGenerator( p_stream, p_actions, m_environment );
    }
}
