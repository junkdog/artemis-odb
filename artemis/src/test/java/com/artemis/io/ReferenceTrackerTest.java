package com.artemis.io;

import com.artemis.EntityHelper;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.EntityHolder;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReferenceTrackerTest {
	@Test
	public void intercept_component_with_entity_references() {
		World world = new World();
		EntityEdit ee = EntityHelper.edit(world, world.createEntity());
		ee.create(ComponentX.class); // not referenced
		ee.create(EntityHolder.class);
		ee.create(ComponentY.class); // not referenced

		ReferenceTracker tracker = new ReferenceTracker(world);
		tracker.inspectTypes(world);

		assertEquals(2, tracker.referenced.size());

		// @todo int
//		EntityReference ref1 = tracker.find(EntityHolder.class, "entity");
//		assertEquals(EntityHolder.class, ref1.componentType);
//		assertEquals("entity", ref1.field.getName());
//		assertEquals(EntityReference.FieldType.ENTITY, ref1.fieldType);

		EntityReference ref2 = tracker.find(EntityHolder.class, "entityId");
		assertEquals(EntityHolder.class, ref2.componentType);
		assertEquals("entityId", ref2.field.getName());
		assertEquals(EntityReference.FieldType.INT, ref2.fieldType);
	}
}