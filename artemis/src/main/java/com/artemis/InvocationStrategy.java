package com.artemis;

import com.artemis.utils.Bag;

/**
 * Simple sequential invocation strategy.
 * @see SystemInvocationStrategy
 */
public class InvocationStrategy extends SystemInvocationStrategy {

	/** Processes all systems in order. */
	@Override
	protected void process(Bag<BaseSystem> systems) {
		Object[] systemsData = systems.getData();
		for (int i = 0, s = systems.size(); s > i; i++) {
			if (disabled.get(i))
				continue;

			BaseSystem system = (BaseSystem) systemsData[i];
			system.process();
			updateEntityStates();
		}
	}
}
