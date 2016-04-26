package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import org.junit.Test;

import static com.artemis.link.LinkFactory.getReferenceType;
import static org.junit.Assert.*;

public class LinkFactoryTest {
	@Test
	public void inspect_no_entity_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttEmpty.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceType(field) > 0) {
				found = field;
			}
		}

		assertEquals(2, fields.length);
		assertNull(found);
	}

	@Test
	public void inspect_entity_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttEntity.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceType(field) > 0) {
				found = field;
				break;
			}
		}

		assertEquals(1, fields.length);
		assertNotNull(found);
	}

	@Test
	public void inspect_entity_id_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttEntityId.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceType(field) > 0) {
				found = field;
				break;
			}
		}

		assertEquals(1, fields.length);
		assertNotNull(found);
	}

	@Test
	public void inspect_entity_bag_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttBagEntity.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceType(field) > 0) {
				found = field;
				break;
			}
		}

		assertEquals(1, fields.length);
		assertNotNull(found);
	}

	@Test
	public void inspect_int_bag_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttIntBag.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceType(field) > 0) {
				found = field;
				break;
			}
		}

		assertEquals(1, fields.length);
		assertNotNull(found);
	}

	static class LttEmpty extends Component {
		public int id;
		public Bag<Void> entities;
	}

	static class LttEntity extends Component {
		public Entity entity;
	}

	static class LttEntityId extends Component {
		@EntityId public int id;
	}

	static class LttBagEntity extends Component {
		public Bag<Entity> entities;
	}

	static class LttIntBag extends Component {
		@EntityId public IntBag ids;
	}
}
