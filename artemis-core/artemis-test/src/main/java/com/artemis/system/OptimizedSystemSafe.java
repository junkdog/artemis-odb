package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.EntityProcessingSystem;

@PreserveProcessVisiblity
public class OptimizedSystemSafe extends EntityProcessingSystem {

	public OptimizedSystemSafe() {
		super(null);
	}

	@Override
	protected void process(Entity e) {}

}
