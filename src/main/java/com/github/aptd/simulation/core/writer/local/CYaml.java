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

package com.github.aptd.simulation.core.writer.local;

import com.github.aptd.simulation.core.writer.IWriter;
import com.github.aptd.simulation.error.CRuntimeException;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * YAML writer
 *
 * @see https://en.wikipedia.org/wiki/YAML
 */
public final class CYaml implements IWriter
{
    /**
     * output file
     */
    private final String m_output;
    /**
     * output data
     */
    private final Map<String, ?> m_data = new HashMap<>();

    /**
     * ctor
     *
     * @param p_output output name
     */
    public CYaml( final String p_output )
    {
        m_output = p_output;
    }


    @Override
    public IWriter section( final int p_depth, final String p_description )
    {
        return this;
    }

    @Override
    public <T> IWriter value( final String p_description, final T p_value )
    {
        return this;
    }

    @Override
    public final IWriter apply()
    {
        try
        {
            new Yaml().dump( m_data, new FileWriter( m_output ) );
        }
        catch ( final IOException l_exception )
        {
            throw new CRuntimeException( l_exception );
        }
        return this;
    }
}
