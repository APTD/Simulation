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

package com.github.aptd.simulation.datamodel;

import com.github.aptd.simulation.core.experiment.IExperiment;
import com.github.aptd.simulation.factory.IFactory;


/**
 * data model components
 */
public interface IDataModel
{

    /**
     * transfer the data-model into an experiment
     * based on given factory
     *
     * @param p_factory building factory
     * @param p_datamodel any representation of the datamodel
     * @return experiment
     */
    IExperiment get( final IFactory p_factory, final String p_datamodel, final long p_simulationsteps, final boolean p_parallel, final String p_timemodel );

}
