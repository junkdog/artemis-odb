package com.artemis.system;


import static org.junit.Assert.fail;

import com.artemis.annotations.Profile;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.util.SimpleProfiler;

@Profile(enabled=true, using=SimpleProfiler.class)
public class ProfiledSystemB extends VoidEntitySystem {

	public int execution;
	
	@Override
	protected void processSystem() {
		
	}
	
	@Override
	protected void begin() {
		execution++;
	}
	
	@Override
	protected void end() {
		if (execution == 1)
			return;
		
		if (execution == 1)
			fail();
	}
}
