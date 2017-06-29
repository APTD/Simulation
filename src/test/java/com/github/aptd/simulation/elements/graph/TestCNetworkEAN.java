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

package com.github.aptd.simulation.elements.graph;

import com.github.aptd.simulation.IBaseTest;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IEventActivityNetwork;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkEvent;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.local.CEventActivityNetwork;
import org.junit.Test;


/**
 * test network EAN
 */
public final class TestCNetworkEAN extends IBaseTest
{
    /**
     * test ean
     */
    @Test
    public final void ean()
    {
        final IEventActivityNetwork<INetworkEvent, INetworkActivity> l_ean = new CEventActivityNetwork();

    }


    /**
     * main
     *
     * @param p_args arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCNetworkEAN().invoketest();
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

}
