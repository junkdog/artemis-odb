package com.artemis.system;

import com.artemis.ComponentMapper;
import com.artemis.component.TransPackedInt;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;

public class BasicVoidSystem extends VoidEntitySystem {

	@Override
	protected void initialize() {
		world.getManager(TagManager.class);
		world.getSystem(WiredBasicVoidSystem.class);
	}
	
	@Override
	protected void processSystem() {
		ComponentMapper<TransPackedInt> mapper1 = world.getMapper(TransPackedInt.class);
	}
}
