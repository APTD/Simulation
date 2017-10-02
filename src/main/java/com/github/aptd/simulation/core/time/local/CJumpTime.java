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

import com.github.aptd.simulation.core.time.IBaseTime;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IElement;
import org.pmw.tinylog.Logger;

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

    private final List<IElement<?>> m_elements = Collections.synchronizedList( new ArrayList<>( ) );

    /**
     * ctor with system default time zone
     *
     * @param p_starttime initial time
     */
    public CJumpTime( final Instant p_starttime )
    {
        this( p_starttime, ZoneId.systemDefault() );
    }

    /**
     * ctor
     *
     * @param p_starttime current time
     * @param p_zone time zone
     */
    public CJumpTime( final Instant p_starttime, final ZoneId p_zone )
    {
        super( p_starttime, p_zone );
    }

    @Override
    public ITime call() throws Exception
    {
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
            Logger.debug( "time advancing to " + l_nextactivation );
        }
        return this;
    }

    @Override
    public void addagent( final IElement<?> p_element )
    {
        m_elements.add( p_element );
    }

    @Override
    public void removeagent( final IElement<?> p_element )
    {
        m_elements.remove( p_element );
    }

}
