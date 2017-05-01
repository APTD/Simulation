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

package com.github.aptd.simulation.ui;

import org.glassfish.jersey.server.ResourceConfig;
import org.lightjason.rest.CCommon;

import java.text.MessageFormat;


/**
 * rest-api servlet
 */
public final class CRestApi extends ResourceConfig
{

    /**
     * ctor
     */
    public CRestApi()
    {
        //this.register();
        this.packages(
            true,
            MessageFormat.format( "{0}.{1}", CCommon.PACKAGEROOT, "container" ),
            "com.fasterxml.jackson.jaxrs.json"
        );

    }

}
