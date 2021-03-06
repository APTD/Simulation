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


import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.graph.IGraph;
import com.github.aptd.simulation.elements.graph.network.IPlatform;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.graph.network.local.CNetwork;
import com.github.aptd.simulation.elements.graph.network.local.CPlatform;
import com.github.aptd.simulation.elements.graph.network.local.CStation;
import com.github.aptd.simulation.elements.graph.network.local.CTransit;
import com.github.aptd.simulation.elements.graph.network.local.CVirtual;
import com.github.aptd.simulation.elements.passenger.CPassenger;
import com.github.aptd.simulation.elements.passenger.CPassengerSource;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.github.aptd.simulation.elements.passenger.IPassengerSource;
import com.github.aptd.simulation.elements.train.CDoor;
import com.github.aptd.simulation.elements.train.CTrain;
import com.github.aptd.simulation.elements.train.IDoor;
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

    @Nonnull
    @Override
    public final IElement.IGenerator<ITrain<?>> train( @Nonnull final InputStream p_stream,
                                                       @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CTrain.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IDoor<?>> door( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions, final ITime p_time
    ) throws Exception
    {
        return new CDoor.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    @SafeVarargs
    @SuppressWarnings( {"varargs", "Guava"} )
    public final IGraph<IStation<?>, ITrack<?>> network( @Nonnull final Stream<ITrack<?>> p_edge,
                                                         @Nullable final Function<ITrack<?>, ? extends Number>... p_weight
    )
    {
        return new CNetwork( p_edge, p_weight == null ? null : p_weight[0] );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> station( @Nonnull final InputStream p_stream,
                                                     @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CStation.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IPlatform<?>> platform( @Nonnull final InputStream p_stream,
                                                       @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CPlatform.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> transit( @Nonnull final InputStream p_stream,
                                                     @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CTransit.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IStation<?>> virtual( @Nonnull final InputStream p_stream,
                                                     @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CVirtual.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IPassengerSource<?>> passengersource( @Nonnull final InputStream p_stream,
                                                                     @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CPassengerSource.CGenerator( p_stream, p_actions, p_time );
    }

    @Nonnull
    @Override
    public IElement.IGenerator<IPassenger<?>> passenger( @Nonnull final InputStream p_stream,
                                                         @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception
    {
        return new CPassenger.CGenerator( p_stream, p_actions, p_time );
    }
}
