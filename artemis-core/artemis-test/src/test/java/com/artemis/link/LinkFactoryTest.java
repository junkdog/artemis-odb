package com.artemis.link;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import org.junit.Test;

import static com.artemis.link.LinkFactory.getReferenceTypeId;
import static org.junit.Assert.*;

public class LinkFactoryTest {
	@Test
	public void inspect_no_entity_reference() {
		Field[] fields = ClassReflection.getDeclaredFields(LttEmpty.class);
		Field found = null;

		for (Field field : fields) {
			if (getReferenceTypeId(field) > 0) {
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
			if (getReferenceTypeId(field) > 0) {
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
			if (getReferenceTypeId(field) > 0) {
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
			if (getReferenceTypeId(field) > 0) {
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
			if (getReferenceTypeId(field) > 0) {
				found = field;
				break;
			}
		}

		assertEquals(1, fields.length);
		assertNotNull(found);
	}

	@Test
	public void create_single_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttEntity.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(1, links.size());

		UniLinkSite link = (UniLinkSite) links.get(0);
		assertEquals("entity", link.field.getName());
	}

	@Test
	public void create_multi_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttEntity.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(1, links.size());

		UniLinkSite link = (UniLinkSite) links.get(0);
		assertEquals("entity", link.field.getName());
	}

	@Test
	public void create_zero_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttMulti.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(4, links.size());
	}

	public static class LttEmpty extends Component {
		public int id;
		public Bag<Void> entities;
	}

	public static class LttEntity extends Component {
		public Entity entity;
	}

	public static class LttEntityId extends Component {
		@EntityId public int id;
	}

	public static class LttBagEntity extends Component {
		public Bag<Entity> entities = new Bag<Entity>();
	}

	public static class LttIntBag extends Component {
		@EntityId public IntBag ids = new IntBag();
	}

	public static class LttMulti extends Component {
		@EntityId public IntBag intIds = new IntBag();
		@EntityId public int id;
		public Entity e;
		public Bag<Entity> entities = new Bag<Entity>();
		public int notMe;
	}
}
