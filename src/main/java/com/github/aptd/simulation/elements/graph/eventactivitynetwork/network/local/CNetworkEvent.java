package com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.local;

import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkActivity;
import com.github.aptd.simulation.elements.graph.eventactivitynetwork.network.INetworkEvent;

import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;


/**
 * network event
 */
public final class CNetworkEvent implements INetworkEvent
{
    private final INetworkActivity m_from;

    private final INetworkActivity m_to;

    public CNetworkEvent( final INetworkActivity p_from, final INetworkActivity p_to )
    {
        m_from = p_from;
        m_to = p_to;
    }



    @Override
    public final INetworkActivity from()
    {
        return m_from;
    }

    @Override
    public final INetworkActivity to()
    {
        return m_from;
    }

    @Override
    public final BiFunction<INetworkActivity, INetworkActivity, Number> cost()
    {
        return (i, j) -> ChronoUnit.MINUTES.between( j.time(), i.time() );
    }
}
