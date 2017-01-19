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

import com.github.aptd.simulation.elements.factory.local.CNetworkEdge;
import com.github.aptd.simulation.scenario.reader.CXMLReader;
import com.github.aptd.simulation.scenario.xml.Asimov;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;


/**
 * scenario XML test
 */
public final class TestCXMLScenario
{
    private Asimov m_scenario;

    /**
     * reads a test scenario
     */
    @Before
    public final void initialize()
    {
        try
        (
            final InputStream l_stream = new FileInputStream( "src/test/resources/scenario.xml" );
        )
        {

            m_scenario = new CXMLReader().get( l_stream );
        }
        catch ( final Exception l_exception )
        {
            assertTrue( l_exception.getMessage(), false );
        }

    }


    /**
     * test graph build with node agents
     */
    @Test
    public final void testAgent()
    {
        m_scenario
            .getNetwork()
            .getInfrastructure()
            .getTracks()
            .getTrack()
            .parallelStream()
            .map( i -> CNetworkEdge.from(
                            i.getTrackTopology().getTrackBegin().getMacroscopicNode().getOcpRef(),
                            i.getTrackTopology().getTrackEnd().getMacroscopicNode().getOcpRef()
            ) ).forEach( System.out::println );
    }


    /**
     * run manual test
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {

        final TestCXMLScenario l_test = new TestCXMLScenario();

        l_test.initialize();
    }
}
