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

package com.github.aptd.simulation.elements;

import org.lightjason.agentspeak.language.ILiteral;

import java.util.stream.Stream;


/**
 * any object which be perceived by an agent
 */
public interface IPerceive
{

    /**
     * get literal of the object
     *
     * @param p_object objects
     * @return stream of literal
     */
    Stream<ILiteral> literal( final IElement<?>... p_object );

    /**
     * get literal of the object
     *
     * @param p_object objects
     * @return stream of literal
     */
    Stream<ILiteral> literal( final Stream<IElement<?>> p_object );

}
