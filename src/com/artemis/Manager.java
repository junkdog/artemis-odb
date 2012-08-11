package com.artemis;


/**
 * Manager.
 * 
 * @author Arni Arent
 * 
 */
public abstract class Manager {
	protected World world;
	
	protected abstract void changed(Entity e);
	
	protected abstract void added(Entity e);

	protected abstract void deleted(Entity e);

	protected abstract void initialize();

	protected void setWorld(World world) {
		this.world = world;
	}

	protected World getWorld() {
		return world;
	}
}
