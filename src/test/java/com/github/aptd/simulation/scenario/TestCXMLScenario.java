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

package com.github.aptd.simulation.scenario;

import com.github.aptd.simulation.IBaseTest;
import com.github.aptd.simulation.datamodel.IDataModel;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;


/**
 * scenario XML test
 */
public final class TestCXMLScenario extends IBaseTest
{
    /**
     * configuration object
     */
    private IDataModel m_scenario;

    /**
     * reads a test scenario
     */
    @Before
    public final void initialize()
    {
        m_scenario = scenarioxmlreader( "src/test/resources/scenario.xml" );
    }


    /**
     * test graph build with node agents
     */
    @Test
    public final void testNetworkWithAgent()
    {
        Assume.assumeNotNull( m_scenario );
    }


    /**
     * run manual test
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCXMLScenario().invoketest();
    }
}
