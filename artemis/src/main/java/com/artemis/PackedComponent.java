package com.artemis;

import java.nio.ByteBuffer;

/**
 * Packs components into a memory-friendly storage, such as a primitive array or {@link ByteBuffer},
 * reuses the same instance for all entities.
 * <p>
 * Requires zero-argument constructor and that, calling the constructor must not change the
 * underlying component data - ie, it's the equivalent of a {@link #clone()}.
 */
public abstract class PackedComponent extends Component {
	protected abstract PackedComponent forEntity(Entity e);
	protected abstract void reset();
}
