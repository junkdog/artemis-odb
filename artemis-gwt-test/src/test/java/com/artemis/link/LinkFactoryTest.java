package com.artemis.link;

import com.artemis.*;
import com.artemis.component.*;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.google.gwt.junit.client.GWTTestCase;

import static com.artemis.link.LinkFactory.getReferenceTypeId;

public class LinkFactoryTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_inspect_no_entity_reference() {
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

	public void test_inspect_entity_reference() {
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

	public void test_inspect_entity_id_reference() {
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

	public void test_inspect_entity_bag_reference() {
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

	public void test_inspect_int_bag_reference() {
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

	public void test_create_single_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttEntity.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(1, links.size());

		UniLinkSite link = (UniLinkSite) links.get(0);
		assertEquals("entity", link.field.getName());
	}

	public void test_create_multi_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttEntity.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(1, links.size());

		UniLinkSite link = (UniLinkSite) links.get(0);
		assertEquals("entity", link.field.getName());
	}

	public void test_create_zero_link_site() {
		World w = new World();
		ComponentTypeFactory typeFactory = w.getComponentManager().getTypeFactory();
		ComponentType ct = typeFactory.getTypeFor(LttMulti.class);

		LinkFactory linkFactory = new LinkFactory(w);
		Bag<LinkSite> links = linkFactory.create(ct);

		assertEquals(4, links.size());
	}

}
