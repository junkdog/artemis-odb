package com.artemis.system;

import com.artemis.WorldConfiguration;
import org.junit.Assert;
import org.junit.Test;

import com.artemis.EntityWorld;
import com.artemis.annotations.Profile;
import com.artemis.util.SimpleProfiler;

@SuppressWarnings("static-method")
public class ProfiledSystemsTest {
	
	@Test
	public void plain_profiled_system_invoked_during_process() {
		EntityWorld world = new EntityWorld(new WorldConfiguration()
				.setSystem(new ProfiledSystem()));

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
		EntityWorld world = new EntityWorld(new WorldConfiguration()
				.setSystem(new ProfiledSystemB()));

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
