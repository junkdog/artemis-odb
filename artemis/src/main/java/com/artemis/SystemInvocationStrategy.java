package com.artemis;

import com.artemis.utils.Bag;

/** Delegate for system invocation.
 *
 * Maybe you want to more granular control over system invocations, feed certain systems different deltas,
 * or completely rewrite processing in favor of events. Extending this class allows you to write your own
 * logic for processing system invocation.
 *
 * Register it with {@link WorldConfigurationBuilder#register(SystemInvocationStrategy)}
 * 
 * Be sure to call {@link #updateEntityStates()} after the world dies.
 *
 * @see InvocationStrategy for the default strategy.
 */
public abstract class SystemInvocationStrategy {

	/** World to operate on. */
	protected World world;

	/** World to operate on. */
	protected final void setWorld(World world) {
		this.world = world;
	}

	/** Called during world initialization phase. */
	protected void initialize() {}

	/** Call to inform all systems and subscription of world state changes. */
	protected final void updateEntityStates() {
		world.batchProcessor.update();
	}

	/** Process all systems. */
	protected abstract void process(Bag<BaseSystem> systems);
}
