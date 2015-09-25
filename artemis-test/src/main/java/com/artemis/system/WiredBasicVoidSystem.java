package com.artemis.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.component.TransPackedInt;
import com.artemis.managers.TagManager;

@SuppressWarnings("unused")
public class WiredBasicVoidSystem extends BaseSystem {

	private TagManager tagManager;
	private BasicVoidSystem voidSystem;
	private ComponentMapper<TransPackedInt> mapper;
	
	@Override
	protected void processSystem() {}
}
