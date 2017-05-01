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

package com.github.aptd.simulation.elements.common;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import com.github.aptd.simulation.elements.IElement;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;

import java.util.Arrays;
import java.util.stream.Stream;


/**
 * matrix wrapper of GPS position, position
 * zero contains latitude and one longitude,
 * structure is unmodifyable
 *
 * @todo chould be modifyable
 */
public class CGPS implements IGPS
{
    /**
     * longitude
     */
    private final DoubleMatrix1D m_position;
    /**
     * literal
     */
    private final ILiteral m_literal;

    /**
     * ctor
     *
     * @param p_longitude longitude
     * @param p_latitude latitude
     */
    public CGPS( final double p_longitude, final double p_latitude )
    {
        m_position = new DenseDoubleMatrix1D( new double[]{p_longitude, p_latitude} );
        m_literal = CLiteral.from( "gps",
                                     CLiteral.from( "longitude", CRawTerm.from( m_position.get( 0 ) ) ),
                                     CLiteral.from( "latitude", CRawTerm.from( m_position.get( 1 ) ) )

        );
    }

    @Override
    public final double longitude()
    {
        return m_position.get( 0 );
    }

    @Override
    public final double latitude()
    {
        return m_position.get( 1 );
    }

    @Override
    public DoubleMatrix1D matrix()
    {
        return m_position;
    }

    @Override
    public final Stream<ILiteral> literal( final IElement<?>... p_object )
    {
        return this.literal( Arrays.stream( p_object ) );
    }

    @Override
    public final Stream<ILiteral> literal( final Stream<IElement<?>> p_object )
    {
        return Stream.of( m_literal );
    }

}
