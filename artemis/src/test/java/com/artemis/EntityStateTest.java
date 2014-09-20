package com.artemis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.artemis.component.Packed;

public class EntityStateTest
{
	private World world;

	@Before
	public void init() {
		world = new World();
	}
	
	@Test @Ignore
	public void disable_enable_test() {
		Assert.fail();
	}
	
	@Test @Ignore
	public void enable_disable_test() {
		Assert.fail();
	}
}
