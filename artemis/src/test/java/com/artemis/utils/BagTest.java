package com.artemis.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("static-method")
public class BagTest
{
	@Test // see https://code.google.com/p/artemis-framework/issues/detail?id=7
	public void set_element_and_ensure_size()
	{
		Bag<Integer> b = new Bag<Integer>();
		for (int i = 0; 10 > i; i++) {
			b.add(i);
		}
		
		b.set(4, -10);
		assertEquals(10, b.size());
	}
}
