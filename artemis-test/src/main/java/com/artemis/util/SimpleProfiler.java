package com.artemis.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.artemis.BaseSystem;
import org.junit.Assert;

import com.artemis.World;
import com.artemis.utils.ArtemisProfiler;

public class SimpleProfiler implements ArtemisProfiler {

	public int startCount;
	public int stopCount;
	
	public static SimpleProfiler lastInstance;
	
	public SimpleProfiler() {
		lastInstance = this;
	}
	
	@Override
	public void start() {
		assertEquals(startCount, stopCount);
		startCount++;
	}

	@Override
	public void stop() {
		assertEquals(startCount - 1, stopCount);
		stopCount++;
	}

	@Override
	public void initialize(BaseSystem owner, World world) {
		System.out.println("\tinitializing " + owner.getClass());
		assertNotNull(owner);
		assertNotNull(world);
	}
	
	public void validate() {
		Assert.assertTrue(startCount > 0);
		assertEquals(startCount, stopCount);
	}
}