package com.artemis;

/**
 * Component type that recycles instances.
 *
 * Expects no <code>final</code> fields.
 *
 * @see com.artemis.annotations.PooledWeaver to automate pooled component creation.
 */
public abstract class PooledComponent extends Component {

	/** Called whenever the component is recycled. Implementation should reset component to pristine state. */
	protected abstract void reset();
}
