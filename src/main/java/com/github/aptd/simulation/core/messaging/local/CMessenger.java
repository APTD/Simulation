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

package com.github.aptd.simulation.core.messaging.local;

import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.core.messaging.IMessenger;
import com.github.aptd.simulation.elements.IElement;

import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * messenger class
 */
public class CMessenger implements IMessenger
{

    private static final boolean OUTPUT_ONLY_FROM_IMMINENT = true;
    private IExperiment m_experiment;

    /**
     * asks agents for output messages and delivers them to the target agents
     *
     * @return self reference
     */
    @Override
    public IMessenger call()
    {
        if ( m_experiment == null )
        {
            Logger.getLogger( this.getClass().getCanonicalName() ).warning( "CMessenger executed without reference to IExperiment, cannot operate" );
            return this;
        }
        optionalfilteredstream( m_experiment.objects().parallel(), OUTPUT_ONLY_FROM_IMMINENT ? IElement::imminent : null )
            .flatMap( IElement::output ).forEach( msg -> m_experiment.getAgent( msg.recipient() ).input( msg ) );
        return this;
    }

    /**
     * set experiment reference
     * @param p_experiment experiment object
     * @return self reference
     */
    public CMessenger experiment( final IExperiment p_experiment )
    {
        m_experiment = p_experiment;
        return this;
    }

    private static <T> Stream<T> optionalfilteredstream( final Stream<T> p_stream, final Predicate<T> p_predicate )
    {
        return p_predicate == null ? p_stream : p_stream.filter( p_predicate );
    }

}
