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

package com.github.aptd.simulation.common;

import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;


/**
 * tool class for defining agent trigger
 */
public final class CAgentTrigger
{


    public static final ITrigger ACTIVATE = CTrigger.from( ITrigger.EType.ADDGOAL, CLiteral.from( "activate" ) );


    /**
     * ctor
     */
    private CAgentTrigger()
    {}


    /**
     * wagon announcement
     *
     * @param p_data announcement data
     * @return trigger
     */
    public static ITrigger wagonannouncement( final Object p_data )
    {
        return CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from( "wagon/announcement", CRawTerm.from( p_data ) )
        );
    }

    /**
     * creates the sender structure of a message
     *
     * @param p_agent agent
     * @return sender literal
     */
    public static ILiteral messagersender( final IAgent<?> p_agent )
    {
        return  CLiteral.from( "from", CRawTerm.from( p_agent.<IElement<?>>raw().id() ) );
    }

    /**
     * create a message send trigger
     *
     * @param p_message message data
     * @param p_agent sender agent
     * @return trigger
     */
    public static ITrigger messagesend( final Object p_message, final IAgent<?> p_agent )
    {
        return messagesend( p_message, messagersender( p_agent ) );
    }

    /**
     * create a message send trigger
     *
     * @param p_message message data
     * @param p_sender sender literal
     * @return trigger
     */
    public static ITrigger messagesend( final Object p_message, final ILiteral p_sender )
    {
        return CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from( "message/receive", CLiteral.from( "message", CRawTerm.from( p_message ) ), p_sender )
        );
    }


}
