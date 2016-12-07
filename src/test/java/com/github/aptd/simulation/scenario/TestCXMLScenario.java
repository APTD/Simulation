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

import com.github.aptd.simulation.model.xml.Asimov;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;


/**
 * scenario XML test
 */
public final class TestCXMLScenario
{

    /**
     * reads a test scenario
     */
    @Test
    // @todo "ignore" bitte heraus nehmen, damit der Test läuft
    @Ignore
    public final void reading()
    {
        try
        (
            final InputStream l_stream = new FileInputStream( "src/test/resources/scenario.xml" );
        )
        {

            final Asimov l_scenario = new CXMLReader().get( l_stream );

            // @todo hier bitte einen Test bauen, d.h. die XML (scenario.xml) mit Beispieldaten befüllen und dann
            // prüfen, ob alles in dem Asimov-Objekt korrekt vorhanden ist
            // siehe http://www.tutego.de/blog/javainsel/2010/04/junit-4-tutorial-java-tests-mit-junit/
        }
        catch ( final Exception l_exception )
        {
            assertTrue( l_exception.getMessage(), false );
        }
    }

    /**
     * run manual test
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCXMLScenario().reading();
    }
}
