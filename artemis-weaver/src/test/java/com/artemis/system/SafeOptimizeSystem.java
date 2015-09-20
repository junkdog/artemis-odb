package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.EntityProcessingSystem;

@PreserveProcessVisiblity
public final class SafeOptimizeSystem extends EntityProcessingSystem {

	public SafeOptimizeSystem(Aspect aspect) {
		super(Aspect.all());
	}

	@Override
	protected void process(int e) {
		System.out.println("hello!");
	}
}
