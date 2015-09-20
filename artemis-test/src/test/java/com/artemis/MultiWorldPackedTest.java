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
		int o = outer.createEntity();
		EntityHelper.edit(outer, o).create(TransPackedInt.class).x(1);
		EntityHelper.edit(outer, o).create(TransPackedFloat.class).x(2);

		World inner = new World();
		int i = inner.createEntity();
		EntityHelper.edit(inner, i).create(TransPackedFloat.class).x(3);
		EntityHelper.edit(inner, i).create(TransPackedInt.class).x(4);

		assertEquals(o, i);
		
		assertEquals(1, EntityHelper.getComponent(TransPackedInt.class, outer, o).x());
		assertEquals(2, EntityHelper.getComponent(TransPackedFloat.class, outer, o).x(), 0.001);
		assertEquals(3, EntityHelper.getComponent(TransPackedFloat.class, inner, i).x(), 0.001);
		assertEquals(4, EntityHelper.getComponent(TransPackedInt.class, inner, i).x());
	}
}
