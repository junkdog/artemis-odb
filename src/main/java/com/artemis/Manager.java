package com.artemis;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 */
public abstract class Manager implements EntityObserver {

	/**
	 * The world associated with this manager.
	 */
	protected World world;

	/**
	 * Called when the world initializes.
	 * <p>
	 * Override to implement custom behavior at initialization.
	 * </p>
	 */
	protected abstract void initialize();

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
	public void added(Entity e) {
	}
	
	@Override
	public void changed(Entity e) {
	}
	
	@Override
	public void deleted(Entity e) {
	}
	
	@Override
	public void disabled(Entity e) {
	}
	
	@Override
	public void enabled(Entity e) {
	}

}
