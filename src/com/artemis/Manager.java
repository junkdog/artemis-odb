package com.artemis;


/**
 * Manager.
 * 
 * @author Arni Arent
 * 
 */
public abstract class Manager {
	protected World world;
	
	protected abstract void added(Entity e);

	protected abstract void removed(Entity e);

	protected abstract void initialize();

	protected void setWorld(World world) {
		this.world = world;
	}

	protected World getWorld() {
		return world;
	}
}
