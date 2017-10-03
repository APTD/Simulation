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

package com.github.aptd.simulation.core.messaging.local;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.elements.IElement;

import java.io.IOException;


/**
 * message class
 */
public class CMessage implements IMessage
{

    private final IElement<?> m_sender;
    private final String m_recipient;
    private final Object[] m_content;
    private final EMessageType m_type;

    /**
     * ctor
     * @param p_sender sending agent
     * @param p_recipient ID of receiving agent
     * @param p_type message type
     * @param p_content content
     */
    public CMessage( final IElement<?> p_sender, final String p_recipient, final EMessageType p_type, final Object...p_content
    )
    {
        m_sender = p_sender;
        m_recipient = p_recipient;
        m_content = p_content;
        m_type = p_type;
    }

    /**
     * get sender agent ID
     *
     * @return agent ID
     */
    @Override
    public IElement<?> sender()
    {
        return m_sender;
    }

    /**
     * get recipient agent ID
     *
     * @return agent ID
     */
    @Override
    public String recipient()
    {
        return m_recipient;
    }

    /**
     * get content of the message
     *
     * @return content string
     */
    @Override
    public Object[] content()
    {
        return m_content;
    }

    /**
     * get message type
     *
     * @return message type
     */
    @Override
    public EMessageType type()
    {
        return m_type;
    }

    @Override
    public void write( final JsonGenerator p_generator ) throws IOException
    {
        p_generator.writeStartObject();
        p_generator.writeStringField( "type", m_type.name() );
        p_generator.writeStringField( "sender", m_sender.id() );
        p_generator.writeStringField( "recipient", m_recipient );
        p_generator.writeArrayFieldStart( "content" );
        for ( final Object l_object : m_content )
        {
            if ( l_object != null ) p_generator.writeObject( l_object );
            else p_generator.writeNull();
        }
        p_generator.writeEndArray();
        p_generator.writeEndObject();
    }
}
