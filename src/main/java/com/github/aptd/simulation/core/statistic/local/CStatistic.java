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

package com.github.aptd.simulation.core.statistic.local;

import com.github.aptd.simulation.core.statistic.IStatistic;
import com.github.aptd.simulation.core.writer.IWriter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * statistic collector
 *
 * @note inner classes defines agent-agents, so
 * data-structures must be thread-safe
 *
 * @bug incomplete
 */
public final class CStatistic implements IStatistic
{
    /**
     * map with statistic objects
     */
    private final Map<String, DescriptiveStatistics> m_data = new ConcurrentHashMap<>();


    @Override
    public final IStatistic write( final IWriter p_writer )
    {
        p_writer.section( 0, "agentstatistic" );
        m_data.forEach( ( p_key, p_value ) -> apply( p_writer, p_key, p_value ) );
        return this;
    }

    @Override
    public final Stream<IAction> action()
    {
        return Stream.of( new CStatisticAction() );
    }

    /**
     * write data
     *
     * @param p_writer writer instance
     * @param p_name section name
     * @param p_statistic statistic value
     */
    private static void apply( final IWriter p_writer, final String p_name, final DescriptiveStatistics p_statistic )
    {
        p_writer.section( 1, p_name );

        p_writer.value( "geometricmean", p_statistic.getGeometricMean() );
        p_writer.value( "kurtosis", p_statistic.getKurtosis() );
        p_writer.value( "max", p_statistic.getMax() );
        p_writer.value( "min", p_statistic.getMin() );
        p_writer.value( "mean", p_statistic.getMean() );
        p_writer.value( "count", p_statistic.getN() );
        p_writer.value( "25-percentile", p_statistic.getPercentile( 0.25 ) );
        p_writer.value( "75-percentile", p_statistic.getPercentile( 0.75 ) );
        p_writer.value( "populationvariance", p_statistic.getPopulationVariance() );
        p_writer.value( "quadraticmean", p_statistic.getQuadraticMean() );
        p_writer.value( "standdeviation", p_statistic.getStandardDeviation() );
        p_writer.value( "skewness", p_statistic.getSkewness() );
        p_writer.value( "sum", p_statistic.getSum() );
        p_writer.value( "sumsequared", p_statistic.getSumsq() );
        p_writer.value( "variance", p_statistic.getVariance() );
    }


    /**
     * agent action for statistic access
     *
     * @code asimov/statistic( "name", 1.3, 7, 8.9, [1, 2, 3] ); @endcode
     */
    private final class CStatisticAction extends IBaseAction
    {

        @Override
        public final IPath name()
        {
            return CPath.from( "asimov/statistic" );
        }

        @Override
        public final int minimalArgumentNumber()
        {
            return 1;
        }

        @Override
        public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return )
        {
            final List<ITerm> l_arguments = CCommon.flatcollection( p_argument ).collect( Collectors.toList() );
            if ( l_arguments.size() < 2 )
                return CFuzzyValue.from( false );

            final DescriptiveStatistics l_statistic = m_data.computeIfAbsent(
                l_arguments.get( 0 ).<String>raw().trim().toLowerCase(),
                ( i ) -> new SynchronizedDescriptiveStatistics()
            );

            l_arguments.stream()
                       .skip( 1 )
                       .map( ITerm::<Number>raw )
                       .mapToDouble( Number::doubleValue )
                       .forEach( l_statistic::addValue );

            return CFuzzyValue.from( true );
        }
    }



}
