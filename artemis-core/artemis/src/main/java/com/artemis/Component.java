package com.artemis;

/**
 * Components are pure data classes with optionally some helper methods.
 * <p/>
 * Extend to create your own components. Decorate with {@link com.artemis.annotations.PooledWeaver}
 * or manually extend {@link PooledComponent} to make the component pooled.
 *
 * @author Arni Arent
 * @see PooledComponent
 * @see com.artemis.annotations.PooledWeaver
 */
public abstract class Component {
}
