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

package com.github.aptd.simulation.elements.passenger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.IStatefulElement;
import com.github.aptd.simulation.elements.graph.network.IStation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.RealDistribution;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


/**
 * passenger source: an agent creating Passenger agents according to statistical distributions
 */
@IAgentAction
public final class CPassengerSource extends IStatefulElement<IPassengerSource<?>>
        implements IPassengerSource<IPassengerSource<?>>
{

    /**
     * serial id
     */
    private static final long serialVersionUID = 5248638076491134344L;
    /**
     * literal functor
     */
    private static final String FUNCTOR = "passengersource";

    private IElement.IGenerator<?> m_generator;
    private IExperiment m_experiment;
    private IStation<?> m_station;

    private RealDistribution m_distribution;
    private long m_startmillis;
    private int m_passengers;
    private int m_passengersgenerated;

    private RealDistribution m_distplatformchangeduration;

    /**
     * ctor
     * @param p_configuration agent configuration
     * @param p_id ...
     * @param p_time time reference
     * @param p_distribution distribution according to which passengers are generated over time (in milliseconds)
     * @param p_startmillis the start timestamp, in milliseconds (to anchor the distribution)
     * @param p_passengers how many passengers will be generated in total
     * @param p_generator agent generator of passenger agent class to be generated
     * @param p_experiment reference to the experiment instance into which the agents will be added
     * @param p_station station agent at which the passengers will begin their lives
     */
    private CPassengerSource(
        final IAgentConfiguration<IPassengerSource<?>> p_configuration,
        final String p_id,
        final ITime p_time,
        final RealDistribution p_distribution,
        final long p_startmillis,
        final int p_passengers,
        final IGenerator<?> p_generator,
        final IExperiment p_experiment,
        final IStation<?> p_station,
        final RealDistribution p_distplatformchangeduration
    )
    {
        super( p_configuration, FUNCTOR, p_id, p_time );
        m_distribution = p_distribution;
        m_startmillis = p_startmillis;
        m_passengers = p_passengers;
        m_generator = p_generator;
        m_experiment = p_experiment;
        m_station = p_station;
        m_distplatformchangeduration = p_distplatformchangeduration;
        m_passengersgenerated = 0;
        m_nextactivation = determinenextstatechange();
    }

    /**
     * define object literal addons
     *
     * @param p_object calling objects
     * @return literal stream
     */
    @Override
    protected Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object )
    {
        return Stream.of();
    }

    protected synchronized Instant determinenextstatechange()
    {
        if ( m_passengersgenerated >= m_passengers ) return Instant.MAX;
        return Instant.ofEpochMilli( m_startmillis + (long) Math.ceil( m_distribution.inverseCumulativeProbability(
                1.0 * ( m_passengersgenerated + 1 ) / m_passengers ) ) );
    }

    protected synchronized boolean updatestate()
    {
        Logger.trace( "generating passenger at " + m_time.current().toString() );
        generatepassenger();
        return true;
    }

    @Override
    protected boolean updatecontinuous( final Duration p_elapsed )
    {
        return false;
    }

    @Override
    protected void writeState( final JsonGenerator p_generator ) throws IOException
    {
        p_generator.writeNumberField( "passengersgenerated", m_passengersgenerated );
    }

    private synchronized void generatepassenger()
    {
        // @todo put passenger somewhere, initialize its state, etc.
        final IElement<?> l_passenger = m_generator.generatesingle( "passenger-" + m_station.id() + "-" + m_passengersgenerated,
                                                                    itinerary(), m_distplatformchangeduration.sample() );
        m_experiment.addAgent( l_passenger.id(), l_passenger );
        m_passengersgenerated++;
    }

    private Stream<CPassenger.CItineraryEntry> itinerary()
    {
        return Stream.of( new CPassenger.CItineraryEntry( "train-134", "toy-node-1", "toy-node-3",
                                                          null, null,
                                                          "toy-node-1-track-3", "toy-node-3-track-2" ),
                          new CPassenger.CItineraryEntry( "train-2368", "toy-node-3", "toy-node-8",
                                                          null, null,
                                                          "toy-node-3-track-1", "toy-node-8-track-1" ) );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generator
     */
    public static final class CGenerator extends IBaseGenerator<IPassengerSource<?>>
    {
        /**
         * object counter
         */
        private static final AtomicLong COUNTER = new AtomicLong();

        /**
         * generator ctor
         * @param p_stream stream
         * @param p_actions action
         * @param p_time time reference
         * @throws Exception on any error
         */
        public CGenerator( final InputStream p_stream, final Set<IAction> p_actions, final ITime p_time )
                throws Exception
        {
            super( p_stream, p_actions, CPassengerSource.class, p_time );
        }

        @Override
        protected final Pair<IPassengerSource<?>, Stream<String>> generate( final Object... p_data )
        {
            return new ImmutablePair<>(
                    new CPassengerSource( m_configuration,
                                          MessageFormat.format( "{0} {1}", FUNCTOR.toLowerCase(), COUNTER.getAndIncrement() ),
                                          m_time,
                                          (RealDistribution) p_data[0], (long) p_data[1], (int) p_data[2],
                                          (IElement.IGenerator<?>) p_data[3], (IExperiment) p_data[4], (IStation<?>) p_data[5],
                                          (RealDistribution) p_data[6]
                    ),
                    Stream.of( FUNCTOR )
            );
        }

        @Override
        public final IGenerator<IPassengerSource<?>> resetcount()
        {
            COUNTER.set( 0 );
            return this;
        }
    }


}
