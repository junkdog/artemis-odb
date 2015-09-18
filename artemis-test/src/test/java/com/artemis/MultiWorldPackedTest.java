package com.artemis;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.artemis.component.TransPackedFloat;
import com.artemis.component.TransPackedInt;

@SuppressWarnings("static-method")
public class MultiWorldPackedTest {
	
	@Test
	public void packed_components_dont_overwrite_in_multiworld() {
		World outer = new World();
		Entity o = outer.createEntity();
		o.edit().create(TransPackedInt.class).x(1);
		o.edit().create(TransPackedFloat.class).x(2);

		World inner = new World();
		Entity i = inner.createEntity();
		i.edit().create(TransPackedFloat.class).x(3);
		i.edit().create(TransPackedInt.class).x(4);

		assertEquals(o.getId(), i.getId());
		
		assertEquals(1, o.getComponent(TransPackedInt.class).x());
		assertEquals(2, o.getComponent(TransPackedFloat.class).x(), 0.001);
		assertEquals(3, i.getComponent(TransPackedFloat.class).x(), 0.001);
		assertEquals(4, i.getComponent(TransPackedInt.class).x());
	}
}
