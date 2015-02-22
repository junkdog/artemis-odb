package com.artemis;

import com.artemis.utils.Bag;

public class InvocationStrategy extends SystemInvocationStrategy {
	@Override
	protected void process(Bag<BaseSystem> systems) {
		Object[] systemsData = systems.getData();
		for (int i = 0, s = systems.size(); s > i; i++) {
			updateEntityStates();

			BaseSystem system = (BaseSystem) systemsData[i];
			if (!system.isPassive()) {
				system.process();
			}
		}
	}
}
