package com.artemis.utils;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("static-method")
public class BagTest
{

	@Test // see https://code.google.com/p/artemis-framework/issues/detail?id=7
	public void set_element_and_ensure_size() {
		Bag<Integer> b = new Bag<Integer>();
		for (int i = 0; 10 > i; i++) {
			b.add(i);
		}
		
		b.set(4, -10);
		assertEquals(10, b.size());
	}

	@Test(expected = ClassCastException.class)
	public void not_typed_getData() {
		Bag<Integer> intBag = new Bag<Integer>();
		intBag.add(1337);
		Integer[] data = intBag.getData();
		fail("huh?" + data);
	}

	@Test
	public void typed_getData() {
		Bag<Integer> intBag = new Bag<Integer>(Integer.class);
		intBag.add(1337);
		Integer[] data = intBag.getData();
		assertNotNull(data);
	}
}
