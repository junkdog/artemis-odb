package com.artemis;

/**
 * Component type that recycles instances.
 * <p>
 * Expects no <code>final</code> fields.
 */
public abstract class PooledComponent extends Component {
	public abstract void reset();
}
