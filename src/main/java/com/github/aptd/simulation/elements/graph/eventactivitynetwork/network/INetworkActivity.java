package com.github.aptd.simulation.elements.graph.eventactivitynetwork.network;

import com.github.aptd.simulation.elements.graph.eventactivitynetwork.IActivity;
import com.github.aptd.simulation.elements.graph.network.IStation;
import com.github.aptd.simulation.elements.train.ITrain;

import java.time.Instant;


/**
 * network activity
 */
public interface INetworkActivity extends IActivity<ITrain, IStation<?>, INetworkEvent>
{
    /**
     * time of the activity
     *
     * @return time
     */
    Instant time();

}
