package com.artemis.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.component.TransPackedInt;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;

@Wire
@SuppressWarnings("unused")
public class WiredBasicVoidSystem extends VoidEntitySystem {

	private TagManager tagManager;
	private BasicVoidSystem voidSystem;
	private ComponentMapper<TransPackedInt> mapper;
	
	@Override
	protected void processSystem() {}
}
