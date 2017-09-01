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

package com.github.aptd.simulation.elements.graph.network.local;

import cern.colt.matrix.DoubleMatrix1D;
import com.github.aptd.simulation.core.time.ITime;
import com.github.aptd.simulation.elements.IBaseElement;
import com.github.aptd.simulation.elements.IElement;
import com.github.aptd.simulation.elements.common.CGPS;
import com.github.aptd.simulation.elements.common.IGPS;
import com.github.aptd.simulation.elements.graph.network.IStation;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.ILiteral;

import java.util.stream.Stream;


/**
 * abstract class of network stations nodes
 */
public abstract class IBaseStation extends IBaseElement<IStation<?>> implements IStation<IStation<?>>
{
    /**
     * literal functor
     */
    protected static final String BASEFUNCTOR = "station";
    /**
     * serial id
     */
    private static final long serialVersionUID = 1448006244531777496L;
    /**
     * GPS position latitude / longitude
     */
    private final IGPS m_position;



    /**
     * ctor
     *
     * @param p_configuration agent configuration
     * @param p_id station identifier
     * @param p_longitude longitude
     * @param p_latitude latitude
     * @param p_time time reference
     */
    protected IBaseStation( final IAgentConfiguration<IStation<?>> p_configuration, final String p_functor, final String p_id,
                            final double p_longitude, final double p_latitude, final ITime p_time )
    {
        super( p_configuration, p_functor, p_id, p_time );
        m_position = new CGPS( p_longitude, p_latitude );
    }

    @Override
    public final DoubleMatrix1D gps()
    {
        return m_position.matrix();
    }

    @Override
    protected final Stream<ILiteral> individualliteral( final Stream<IElement<?>> p_object
    )
    {
        return m_position.literal( p_object );
    }

}
