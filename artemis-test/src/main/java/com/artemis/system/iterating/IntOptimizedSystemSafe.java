package com.artemis.system.iterating;

import com.artemis.Entity;
import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.EntityProcessingSystem;

@PreserveProcessVisiblity
public class IntOptimizedSystemSafe extends EntityProcessingSystem {

	public IntOptimizedSystemSafe() {
		super(null);
	}

	@Override
	protected void process(Entity e) {}

}
