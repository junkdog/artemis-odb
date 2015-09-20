package com.artemis.system;

import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.EntityProcessingSystem;

@PreserveProcessVisiblity
public class OptimizedSystemSafe extends EntityProcessingSystem {

	public OptimizedSystemSafe() {
		super(null);
	}

	@Override
	protected void process(int e) {}

}
