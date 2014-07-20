package com.artemis.matrix.system;


import com.artemis.annotations.Profile;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.util.SimpleProfiler;

@Profile(enabled=true, using=SimpleProfiler.class)
public class ProfiledSystem extends VoidEntitySystem {

	@Override
	protected void processSystem() {
		
	}
}
