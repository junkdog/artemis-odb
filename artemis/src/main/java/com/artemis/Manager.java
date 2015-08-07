package com.artemis;

import com.artemis.utils.IntBag;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 */
public abstract class Manager implements EntityObserver {

	/** The world associated with this manager. */
	protected World world;

	/**
	 * Called when the world initializes.
	 * <p>
	 * Override to implement custom behavior at initialization.
	 * </p>
	 */
	protected void initialize() {}

	/**
	 * Set the world associated with the manager.
	 *
	 * @param world
	 *			the world to set
	 */
	protected void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Get the world associated with the manager.
	 *
	 * @return the associated world
	 */
	protected World getWorld() {
		return world;
	}

	@Override
	public void added(int entityId) {}

	@Override
	public void changed(int entityId) {}

	@Override
	public void deleted(int entityId) {}
	
	@Override
	public void disabled(int entityId) {}

	@Override
	public void enabled(int entityId) {}
	
	@Override
	public final void added(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(ids[i]);
		}
	}

	@Override
	public final void changed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			changed(ids[i]);
		}
	}

	@Override
	public final void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(ids[i]);
		}
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}
}
