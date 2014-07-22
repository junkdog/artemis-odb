package com.artemis.system;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.World;
import com.artemis.annotations.Profile;
import com.artemis.util.SimpleProfiler;

@SuppressWarnings("static-method")
public class ProfiledSystemsTest {
	
	@Test
	public void plain_profiled_system_invoked_during_process() {
		World world = new World();
		world.setSystem(new ProfiledSystem());
		world.initialize();
		
		Assert.assertNull(
				ProfiledSystem.class.getAnnotation(Profile.class));
		
		world.process();
		world.process();
		
		SimpleProfiler simpleProfiler = SimpleProfiler.lastInstance;
		Assert.assertNotNull(simpleProfiler);
		
		simpleProfiler.validate();
		
		Assert.assertEquals(2, simpleProfiler.startCount);
		Assert.assertEquals(2, simpleProfiler.stopCount);
		
		world.process();
		Assert.assertEquals(3, simpleProfiler.startCount);
		Assert.assertEquals(3, simpleProfiler.stopCount);
	}
	
	@Test
	public void multiple_exit_points_profiled_system() {
		World world = new World();
		world.setSystem(new ProfiledSystemB());
		world.initialize();
		
		Assert.assertNull(
				ProfiledSystemB.class.getAnnotation(Profile.class));
		
		world.process();
		
		SimpleProfiler simpleProfiler = SimpleProfiler.lastInstance;
		Assert.assertNotNull(simpleProfiler);
		
		simpleProfiler.validate();
		
		Assert.assertEquals(1, simpleProfiler.startCount);
		Assert.assertEquals(1, simpleProfiler.stopCount);
		
		world.process();
		Assert.assertEquals(2, simpleProfiler.startCount);
		Assert.assertEquals(2, simpleProfiler.stopCount);
	}
}
