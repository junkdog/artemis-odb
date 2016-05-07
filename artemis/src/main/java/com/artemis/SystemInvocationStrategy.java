package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

import java.util.BitSet;

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
	protected final BitSet disabled = new BitSet();

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

	public boolean isEnabled(BaseSystem system) {
		Class<? extends BaseSystem> target = system.getClass();
		ImmutableBag<BaseSystem> systems = world.getSystems();
		for (int i = 0; i < systems.size(); i++) {
			if (target == systems.get(i).getClass())
				return !disabled.get(i);
		}

		throw new RuntimeException("huh?");
	}

	public void setEnabled(BaseSystem system, boolean value) {
		Class<? extends BaseSystem> target = system.getClass();
		ImmutableBag<BaseSystem> systems = world.getSystems();
		for (int i = 0; i < systems.size(); i++) {
			if (target == systems.get(i).getClass())
				disabled.set(i, !value);
		}
	}
}
