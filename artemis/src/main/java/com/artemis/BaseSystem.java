package com.artemis;

public abstract class BaseSystem {
	/** The world this system belongs to. */
	protected World world;
	/** If the system is passive or not. */
	boolean passive;
	/** If the system is enabled or not. */
	boolean enabled = true;

	public BaseSystem() {}

	/**
	 * Called before system processing begins.
	 * <p>
	 * <b>Nota Bene:</b> Any entities created in this method
	 * won't become active until the next system starts processing
	 * or when a new processing rounds begins, whichever comes first.
	 * </p>
	 */
	protected void begin() {}

	public final void process() {
		if(enabled && checkProcessing()) {
			begin();
			processSystem();
			end();
		}
	}

	/**
	 * Any implementing entity system must implement this method.
	 */
	protected abstract void processSystem();

	/**
	 * Called after the systems has finished processing.
	 */
	protected void end() {}

	/**
	 * Check if the system should be processed.
	 *
	 * @return true if the system should be processed, false if not.
	 */
	@SuppressWarnings("static-method")
	protected boolean checkProcessing() {
		return true;
	}

	/**
	 * Override to implement code that gets executed when systems are
	 * initialized.
	 */
	protected void initialize() {}

	/**
	 * Returns true if the system is enabled.
	 *
	 * @return {@code true} if enabled, otherwise false
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enabled systems are run during {@link #process()}.
	 * <p>
	 * Systems are enabled by default.
	 * </p>
	 *
	 * @param enabled
	 *			system will not run when set to false
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Set the world this manager works on.
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


	/**
	 * Check if this system is passive.
	 * <p>
	 * A passive system will not process when {@link com.artemis.World#process()}
	 * is called.
	 * </p>
	 *
	 * @return {@code true} if the system is passive
	 */
	public boolean isPassive() {
		return passive;
	}

	/**
	 * Set if the system is passive or not.
	 * <p>
	 * A passive system will not process when {@link com.artemis.World#process()}
	 * is called.
	 * </p>
	 *
	 * @param passive
	 *			{@code true} if passive, {@code false} if not
	 */
	protected void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}

	/**
	 * Edit entity.
	 *
	 * @param id
	 * @return EntityHelper Editor for id.
	 */
	public EntityEdit edit( int id ) {
		return world.editPool.obtainEditor(id);
	}

}
