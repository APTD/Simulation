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

package com.github.aptd.simulation.core.time.local;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.aptd.simulation.core.time.IBaseTime;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import org.pmw.tinylog.Logger;

import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Time mechanism for discrete time advancing to the minimum next activation time of all agents (may remain at a time instant for multiple calls)
 */
public class CJumpTime extends IBaseTime
{

    private boolean m_active = true;

    private final List<IElement<?>> m_elements = Collections.synchronizedList( new ArrayList<>( ) );
    private final JsonFactory m_factory = new MappingJsonFactory();

    /**
     * ctor with system default time zone
     *
     * @param p_starttime initial time
     */
    public CJumpTime( final Instant p_starttime, final long p_steplimit )
    {
        this( p_starttime, p_steplimit, ZoneId.systemDefault() );
    }

    /**
     * ctor
     *
     * @param p_starttime current time
     * @param p_zone time zone
     */
    public CJumpTime( final Instant p_starttime, final long p_steplimit, final ZoneId p_zone )
    {
        super( p_starttime, p_steplimit, p_zone );
    }

    @Override
    public ITime call() throws Exception
    {
        final StringWriter l_writer = new StringWriter();
        JsonGenerator l_generator = null;
        try
        {
            l_generator = m_factory.createGenerator( l_writer );
            l_generator.writeStartObject();
            l_generator.writeStringField( "type", "simulationstep" );
            l_generator.writeNumberField( "old_step", m_stepcount.longValue() );
            l_generator.writeStringField( "old_time", m_currenttime.get().toString() );
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
        }

        final Instant l_nextactivation = m_elements
            .parallelStream()
            .map( IElement::nextactivation )
            .min( Comparator.naturalOrder() )
            .orElse( m_currenttime.get() );
        if ( l_nextactivation.isBefore( m_currenttime.get() ) )
            throw new RuntimeException( "minimum next activation " + l_nextactivation + " is before current time " + m_currenttime.get() );
        if ( l_nextactivation.isBefore( Instant.MAX ) )
        {
            m_currenttime.set( l_nextactivation );
            m_active = true;
        }
        else m_active = false;
        super.call();
        if ( l_generator != null )
            try
            {
                l_generator.writeNumberField( "new_step", m_stepcount.longValue() );
                l_generator.writeStringField( "new_time", m_currenttime.get().toString() );
                l_generator.writeBooleanField( "active", m_active );
                l_generator.writeEndObject();
                l_generator.close();
                l_writer.close();
                Logger.debug( l_writer.toString() );
            }
            catch ( final Exception l_exception )
            {
                l_exception.printStackTrace();
            }

        return this;
    }

    @Override
    public void addagent( final IElement<?> p_element )
    {
        m_elements.add( p_element );
        m_active = true;
    }

    @Override
    public void removeagent( final IElement<?> p_element )
    {
        m_elements.remove( p_element );
    }

    @Override
    public boolean active()
    {
        return m_active && super.active();
    }

}
