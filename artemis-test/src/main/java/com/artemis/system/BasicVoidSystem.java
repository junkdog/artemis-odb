package com.artemis.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.component.TransPackedInt;
import com.artemis.managers.TagManager;

@SuppressWarnings("unused")
public class BasicVoidSystem extends BaseSystem {

	private TagManager manager;
	private WiredBasicVoidSystem system;
	private ComponentMapper<TransPackedInt> mapper1;

	@Override
	protected void initialize() {
		manager = world.getSystem(TagManager.class);
		system = world.getSystem(WiredBasicVoidSystem.class);
		mapper1 = world.getMapper(TransPackedInt.class);
	}
	
	@Override
	protected void processSystem() {
		mapper1 = world.getMapper(TransPackedInt.class);
	}
}
