package com.github.aptd.simulation.elements;

import org.lightjason.agentspeak.language.ILiteral;

import java.util.stream.Stream;


/**
 * Created by pkraus on 28.04.17.
 */
public interface IPerceive
{

    /**
     * get literal of the object
     *
     * @param p_object objects
     * @return stream of literal
     */
    Stream<ILiteral> literal( final IElement<?>... p_object );

    /**
     * get literal of the object
     *
     * @param p_object objects
     * @return stream of literal
     */
    Stream<ILiteral> literal( final Stream<IElement<?>> p_object );

}
