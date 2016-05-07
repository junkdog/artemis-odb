package com.artemis;

/**
 * Most basic system.
 *
 * Upon calling world.process(), your systems are processed in sequence.
 *
 * Flow:
 * {@link #initialize()} - Initialize your system, on top of the dependency injection.
 * {@link #begin()} - Called before the entities are processed.
 * {@link #processSystem()} - Called once per cycle.
 * {@link #end()} - Called after the entities have been processed.
 * 
 * @see com.artemis.annotations.Wire
 */
public abstract class BaseSystem {
	/** The world this system belongs to. */
	protected World world;

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

	/**
	 * Process system.
	 *
	 * Does nothing if {@link #checkProcessing()} is false or the system
	 * is disabled.
	 *
	 * @see InvocationStrategy
	 */
	public final void process() {
		if(checkProcessing()) {
			begin();
			processSystem();
			end();
		}
	}

	/**
	 * Process the system.
	 */
	protected abstract void processSystem();

	/**
	 * Called after the systems has finished processing.
	 */
	protected void end() {}

	/**
	 * Does the system desire processing.
	 *
	 * Useful when the system is enabled, but only occasionally
	 * needs to process.
	 *
	 * This only affects processing, and does not affect events
	 * or subscription lists.
	 *
	 * @return true if the system should be processed, false if not.
	 * @see #isEnabled() both must be true before the system will process.
	 */
	@SuppressWarnings("static-method")
	protected boolean checkProcessing() {
		return true;
	}

	/**
	 * Override to implement code that gets executed when systems are
	 * initialized.
	 *
	 * Note that artemis native types like systems, factories and
	 * component mappers are automatically injected by artemis.
	 */
	protected void initialize() {}

	/**
	 * Check if the system is enabled.
	 *
	 * @return {@code true} if enabled, otherwise false
	 */
	public boolean isEnabled() {
		return world.partition.invocationStrategy.isEnabled(this);
	}

	/**
	 * Enabled systems run during {@link #process()}.
	 *
	 * This only affects processing, and does not affect events
	 * or subscription lists.
	 *
	 * Systems are enabled by default.
	 *
	 * @param enabled
	 *			system will not run when set to false
	 * @see #checkProcessing() both must be true before the system will process.
	 */
	public void setEnabled(boolean enabled) {
		world.partition.invocationStrategy.setEnabled(this, enabled);
	}

	/**
	 * Set the world this system works on.
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
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}
}
