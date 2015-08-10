package com.artemis.io;

import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.EntityHolder;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("static-method")
public class ReferenceTrackerTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_intercept_component_with_entity_references() {
		World w = new World();
		EntityEdit ee = w.createEntity().edit();
		ee.create(ComponentX.class); // not referenced
		ee.create(EntityHolder.class);
		ee.create(ComponentY.class); // not referenced

		ReferenceTracker tracker = new ReferenceTracker();
		tracker.inspectTypes(w);

		assertEquals(2, tracker.referenced.size());

		EntityReference ref1 = tracker.find(EntityHolder.class, "entity");
		assertEquals(EntityHolder.class, ref1.componentType);
		assertEquals("entity", ref1.field.getName());
		assertEquals(EntityReference.FieldType.ENTITY, ref1.fieldType);

		EntityReference ref2 = tracker.find(EntityHolder.class, "entityId");
		assertEquals(EntityHolder.class, ref2.componentType);
		assertEquals("entityId", ref2.field.getName());
		assertEquals(EntityReference.FieldType.INT, ref2.fieldType);
	}
}
