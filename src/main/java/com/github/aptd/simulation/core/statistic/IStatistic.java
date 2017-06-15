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

package com.github.aptd.simulation.core.statistic;

import com.github.aptd.simulation.core.writer.IWriter;
import org.lightjason.agentspeak.action.IAction;

import javax.annotation.Nonnull;
import java.util.stream.Stream;


/**
 * statistic evaluation of an experiment
 *
 * @bug incomplete
 */
public interface IStatistic
{
    IStatistic EMPTY = new IStatistic()
    {
        @Nonnull
        @Override
        public final IStatistic write( @Nonnull final IWriter p_writer )
        {
            return this;
        }

        @Nonnull
        @Override
        public final Stream<IAction> action()
        {
            return Stream.empty();
        }
    };

    /**
     * export statistic data
     *
     * @param p_writer writer instance
     * @return self-reference
     */
    @Nonnull
    IStatistic write( @Nonnull final IWriter p_writer );

    /**
     * agent action of statistic
     *
     * @return action stream
     */
    @Nonnull
    Stream<IAction> action();

}
