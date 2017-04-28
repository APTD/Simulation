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

package scenario;

import com.github.aptd.simulation.scenario.xml.AgentRef;
import com.github.aptd.simulation.scenario.xml.Asimov;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;


/**
 * scenario XML reader
 */
public final class CXMLReader implements IReader<Asimov>
{
    /**
     * Jaxb marshalling / unmarshalling context
     */
    private final JAXBContext m_context;

    /**
     * ctor
     * @throws JAXBException is thrown on any jaxb exception
     */
    public CXMLReader() throws JAXBException
    {
        m_context = JAXBContext.newInstance( Asimov.class, AgentRef.class );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final Asimov get( final InputStream p_stream ) throws Exception
    {
        return (Asimov) m_context.createUnmarshaller().unmarshal( p_stream );
    }
}
