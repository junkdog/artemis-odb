package com.artemis;

import java.nio.ByteBuffer;

/**
 * Packs components into a memory-friendly storage, such as a primitive array or {@link ByteBuffer},
 * reuses the same instance for all entities.
 * <p>
 * Constructor must either be zero-length or take a {@link World} instance.
 * Calling the constructor must not change the underlying component data - ie,
 * it's the equivalent of a {@link #clone()}.
 */
public abstract class PackedComponent extends Component {
	protected abstract PackedComponent forEntity(Entity e);
	protected abstract void reset();
	
	public static interface DisposedWithWorld {
		void free(World world);
	}
}
