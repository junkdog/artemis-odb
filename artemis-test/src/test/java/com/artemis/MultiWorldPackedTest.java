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
		outer.initialize();
		Entity o = outer.createEntity();
		o.createComponent(TransPackedInt.class).x(1);
		o.createComponent(TransPackedFloat.class).x(2);
		o.addToWorld();
		
		World inner = new World();
		inner.initialize();
		Entity i = inner.createEntity();
		i.createComponent(TransPackedFloat.class).x(3);
		i.createComponent(TransPackedInt.class).x(4);
		i.addToWorld();
		
		assertEquals(o.getId(), i.getId());
		
		assertEquals(1, o.getComponent(TransPackedInt.class).x());
		assertEquals(2, o.getComponent(TransPackedFloat.class).x(), 0.001);
		assertEquals(3, i.getComponent(TransPackedFloat.class).x(), 0.001);
		assertEquals(4, i.getComponent(TransPackedInt.class).x());
	}
}
