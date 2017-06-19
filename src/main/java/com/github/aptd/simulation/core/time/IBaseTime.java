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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


/**
 * Base implementation of time mechanism with member variable and literal representation
 */
public abstract class IBaseTime implements ITime
{
    /**
     * current time
     */
    protected final AtomicReference<Instant> m_currenttime;
    /**
     * time zone
     */
    private final ZoneId m_zone;

    /**
     * ctor
     *
     * @param p_currenttime current time
     * @param p_zone time zone
     */
    protected IBaseTime( final Instant p_currenttime, final ZoneId p_zone )
    {
        m_currenttime = new AtomicReference<>( p_currenttime );
        m_zone = p_zone;
    }

    @Override
    public final Instant current()
    {
        return m_currenttime.get();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <N extends ITime> N raw()
    {
        return (N) this;
    }

    /**
     * get literal of the current time
     *
     * @param p_object objects
     * @return stream of literal representation of current time
     */
    @Override
    public final Stream<ILiteral> literal( final IElement<?>... p_object )
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
        final ZonedDateTime l_time = m_currenttime.get().atZone( m_zone );
        return Stream.of(
             CLiteral.from( "year", CRawTerm.from( l_time.get( ChronoField.YEAR ) ) ),
             CLiteral.from( "month", CRawTerm.from( l_time.get( ChronoField.MONTH_OF_YEAR ) ) ),
             CLiteral.from( "day", CRawTerm.from( l_time.get( ChronoField.DAY_OF_MONTH ) ) ),
             CLiteral.from( "hours", CRawTerm.from( l_time.get( ChronoField.HOUR_OF_DAY ) ) ),
             CLiteral.from( "minutes", CRawTerm.from( l_time.get( ChronoField.MINUTE_OF_HOUR ) ) ),
             CLiteral.from( "seconds", CRawTerm.from( l_time.get( ChronoField.SECOND_OF_MINUTE ) ) ),
             CLiteral.from( "nanoseconds", CRawTerm.from( l_time.get( ChronoField.NANO_OF_SECOND ) ) ),
             CLiteral.from( "datetime", CRawTerm.from( l_time ) )
        );
    }
}
