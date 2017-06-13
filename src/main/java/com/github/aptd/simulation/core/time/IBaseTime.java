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

package com.github.aptd.simulation.core.time;

import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.stream.Stream;


/**
 * Base implementation of time mechanism with member variable and literal representation
 */
public abstract class IBaseTime implements ITime
{

    protected Instant m_currenttime = Instant.now();

    public Instant current()
    {
        return m_currenttime;
    }

    public ITime call()
    {
        return this;
    }

    /**
     * get literal of the current time
     *
     * @param p_object objects
     * @return stream of literal representation of current time
     */
    @Override
    public Stream<ILiteral> literal( final IElement<?>... p_object )
    {
        return this.literal( Arrays.stream( p_object ) );
    }

    /**
     * get literal of the current time
     *
     * @param p_object objects
     * @return stream of literal representation of current time
     */
    @Override
    public Stream<ILiteral> literal( final Stream<IElement<?>> p_object )
    {
        return Stream.of( CLiteral.from( "time",
                                         CLiteral.from( "year", CRawTerm.from( m_currenttime.get( ChronoField.YEAR ) ) ),
                                         CLiteral.from( "month", CRawTerm.from( m_currenttime.get( ChronoField.MONTH_OF_YEAR ) ) ),
                                         CLiteral.from( "day", CRawTerm.from( m_currenttime.get( ChronoField.DAY_OF_MONTH ) ) ),
                                         CLiteral.from( "hours", CRawTerm.from( m_currenttime.get( ChronoField.HOUR_OF_DAY ) ) ),
                                         CLiteral.from( "minutes", CRawTerm.from( m_currenttime.get( ChronoField.MINUTE_OF_HOUR ) ) ),
                                         CLiteral.from( "seconds", CRawTerm.from( m_currenttime.get( ChronoField.SECOND_OF_MINUTE ) ) ),
                                         CLiteral.from( "nanoseconds", CRawTerm.from( m_currenttime.get( ChronoField.NANO_OF_SECOND ) ) )
                                       )
                        );
    }
}