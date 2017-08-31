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

import com.github.aptd.simulation.core.messaging.EMessageType;
import com.github.aptd.simulation.core.messaging.IMessage;
import com.github.aptd.simulation.elements.IElement;


/**
 * message class
 */
public class CMessage implements IMessage
{

    private final IElement<?> m_sender;
    private final String m_recipient;
    private final String m_content;
    private final EMessageType m_type;

    /**
     * ctor
     * @param p_sender sending agent
     * @param p_recipient ID of receiving agent
     * @param p_content content
     * @param p_type message type
     */
    public CMessage( final IElement<?> p_sender, final String p_recipient, final String p_content,
                     final EMessageType p_type
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
    public String content()
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
}
