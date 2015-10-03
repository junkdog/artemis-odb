package com.artemis.system.iterating;


import com.artemis.Aspect;
import com.artemis.annotations.Profile;
import com.artemis.systems.IteratingSystem;
import com.artemis.util.SimpleProfiler;

@Profile(enabled=true, using=SimpleProfiler.class)
public class IntProfiledSystem extends IteratingSystem {
	public IntProfiledSystem() {
		super(Aspect.all());
	}

	@Override
	protected void process(int e) {

	}
}
