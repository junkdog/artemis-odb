package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.IteratingSystem;

@PreserveProcessVisiblity
public final class IteratingSafeOptimizeSystem extends IteratingSystem {

	public IteratingSafeOptimizeSystem(Aspect aspect) {
		super(Aspect.all());
	}

	@Override
	protected void process(int e) {
		System.out.println("hello!");
	}
}
