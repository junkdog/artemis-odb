package com.artemis.managers;

import com.artemis.EntityWorld;
import com.artemis.WorldConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagManagerTest {

	@Test
	public void test_overwriting_tag() throws Exception {
		EntityWorld w = new EntityWorld(new WorldConfiguration()
			.setSystem(TagManager.class));

		TagManager tags = w.getSystem(TagManager.class);

		int id1 = w.create();
		tags.register("tag", id1);
		w.process();

		assertEquals(id1, tags.getEntityId("tag"));

		int id2 = w.create();
		tags.register("tag", id2);
		w.process();

		assertEquals(id2, tags.getEntityId("tag"));

		w.delete(id1);
		w.process();

		assertEquals(id2, tags.getEntityId("tag"));
	}
}