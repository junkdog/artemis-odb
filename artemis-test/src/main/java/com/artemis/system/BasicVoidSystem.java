package com.artemis.system;

import com.artemis.ComponentMapper;
import com.artemis.component.TransPackedInt;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;

@SuppressWarnings("unused")
public class BasicVoidSystem extends VoidEntitySystem {

	private TagManager manager;
	private WiredBasicVoidSystem system;
	private ComponentMapper<TransPackedInt> mapper1;

	@Override
	protected void initialize() {
		manager = world.getManager(TagManager.class);
		system = world.getSystem(WiredBasicVoidSystem.class);
		mapper1 = world.getMapper(TransPackedInt.class);
	}
	
	@Override
	protected void processSystem() {
		mapper1 = world.getMapper(TransPackedInt.class);
	}
}
