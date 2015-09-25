package com.artemis.system.iterating;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.annotations.Profile;
import com.artemis.system.ProfiledSystem;
import com.artemis.system.ProfiledSystemB;
import com.artemis.util.SimpleProfiler;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("static-method")
public class IntProfiledSystemsTest {
	
	@Test
	public void plain_profiled_system_invoked_during_process() {
		World world = new World(new WorldConfiguration()
				.setSystem(IntProfiledSystem.class));

		Assert.assertNull(
				IntProfiledSystem.class.getAnnotation(Profile.class));
		
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
}
