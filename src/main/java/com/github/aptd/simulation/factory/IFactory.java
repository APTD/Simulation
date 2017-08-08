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
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.graph.network.ITrack;
import com.github.aptd.simulation.elements.passenger.IPassenger;
import com.github.aptd.simulation.elements.passenger.IPassengerSource;
import com.github.aptd.simulation.elements.train.ITrain;
import com.google.common.base.Function;
import org.lightjason.agentspeak.action.IAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;


/**
 * factory to generate all simulation entities
 *
 * @bug incomplete
 */
public interface IFactory
{

    /**
     * creates a train generator
     *
     * @param p_stream ASL stream
     * @param p_actions default actions
     * @param p_time time reference
     * @return generator
     * @throws Exception is thrown on any error
     */
    IElement.IGenerator<ITrain<?>> train( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception;

    /**
     * network generating
     *
     * @param p_edge edges
     * @param p_weight optional weight functions
     * @return network graph
     */
    @Nonnull
    @SuppressWarnings( {"unchecked", "Guava"} )
    IGraph<IStation<?>, ITrack<?>> network( @Nonnull final Stream<ITrack<?>> p_edge, @Nullable final Function<ITrack<?>, ? extends Number>... p_weight );

    /**
     * creates a full station generator,
     * which is used to simulate also the
     * station in detail
     *
     * @param p_stream ASL stream
     * @param p_actions default actions
     * @param p_time time reference
     * @return generator
     * @throws Exception is thrown on any error
     */
    @Nonnull
    IElement.IGenerator<IStation<?>> station( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception;

    /**
     * creates a transit station generator,
     * which is used to simulate demand only
     *
     * @param p_stream ASL stream
     * @param p_actions default actions
     * @param p_time time reference
     * @return generator
     * @throws Exception is thrown on any error
     */
    @Nonnull
    IElement.IGenerator<IStation<?>> transit( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception;

    /**
     * creates a virtual station generator,
     * which is used to simulate events only
     *
     * @param p_stream ASL stream
     * @param p_actions default actions
     * @param p_time time reference
     * @return generator
     * @throws Exception is thrown on any error
     */
    @Nonnull
    IElement.IGenerator<IStation<?>> virtual( @Nonnull final InputStream p_stream, @Nonnull final Set<IAction> p_actions, final ITime p_time ) throws Exception;

    /**
     * creates a PassengerSource generator,
     * which is used to generate passengers
     *
     * @param p_stream ASL stream
     * @param p_actions default actions
     * @param p_time time reference
     * @return generator
     * @throws Exception is thrown on any error
     */
    @Nonnull
    IElement.IGenerator<IPassengerSource<?>> passengersource(
            @Nonnull InputStream p_stream, @Nonnull Set<IAction> p_actions, ITime p_time )
            throws Exception;

    @Nonnull
    IElement.IGenerator<IPassenger<?>> passenger(
            @Nonnull InputStream p_stream, @Nonnull Set<IAction> p_actions, ITime p_time )
            throws Exception;
}
