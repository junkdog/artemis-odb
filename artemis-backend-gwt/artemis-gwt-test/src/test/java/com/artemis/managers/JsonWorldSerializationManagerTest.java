package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.google.gwt.junit.client.GWTTestCase;

import java.io.*;

@Wire(injectInherited = false)
public class JsonWorldSerializationManagerTest extends GWTTestCase {

	private JsonArtemisSerializer backend;

	private static class StandardCharsets {
		public static final String UTF_8 = "UTF-8";
	}

	private WorldSerializationManager manger;
	private AspectSubscriptionManager subscriptions;
	private TagManager tags;
	private GroupManager groups;
	private EntityWorld world;
	private EntitySubscription allEntities;

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	@Override
	public void gwtSetUp() {
		setupWorld();

		allEntities = subscriptions.get(Aspect.all());

		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class).text = "hello";
		ee.create(ComponentY.class).text = "whatever";
		ee.create(ReusedComponent.class);

		EntityEdit ee2 = world.createEntity().edit();
		ee2.create(ComponentX.class).text = "hello 2";
		ee2.create(NameComponent.class).name = "do i work?";
		ee2.create(ComponentY.class).text = "whatever 2";
		ee2.create(ReusedComponent.class);

		EntityEdit ee3 = world.createEntity().edit();
		ee3.create(ComponentX.class).text = "hello 3";
		ee3.create(ComponentY.class).text = "whatever 3";
		ee3.create(ReusedComponent.class);

		world.process();
		assertEquals(3, allEntities.getEntities().size());
	}

	private void setupWorld() {
		world = new EntityWorld(new WorldConfiguration()
				.setSystem(GroupManager.class)
				.setSystem(TagManager.class)
				.setSystem(WorldSerializationManager.class));

		world.inject(this);
		backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		manger.setSerializer(backend);
	}

	public void test_save_compact_json() throws Exception {
		backend.prettyPrint(false);
		save(allEntities);
	}

	public void test_serializer_save_load_std_format_new_world() throws Exception {
		String json = save(allEntities);

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));

		setupWorld();

		allEntities = subscriptions.get(Aspect.all());

		SaveFileFormat load = manger.load(is, SaveFileFormat.class);

		world.process();
		assertEquals(3, allEntities.getEntities().size());

		String json2 = save(allEntities);

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());
	}

	/*
	public void test_serializer_save_to_file_and_load_std_format_new_world() throws Exception {
		String json = save(allEntities);
		PrintWriter writer = new PrintWriter("save_temp.json", "UTF-8");
		writer.append(json);
		writer.close();

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());

		File file = new File("save_temp.json");
		InputStream is = new FileInputStream(file);
		setupWorld();

		allEntities = subscriptions.get(Aspect.all());

		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		file.delete();

		world.process();
		assertEquals(3, allEntities.getEntities().size());

		String json2 = save(allEntities);

		deleteAll();
		assertEquals(0, allEntities.getEntities().size());

	}*/

	public void test_serializer_save_load_with_tags() throws Exception {
		setTags();

		String json = save(allEntities);

		deleteAll();

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		world.process();

		assertEquals(3, allEntities.getEntities().size());
		assertTags();
	}

	public void test_serializer_save_load_with_groups() throws Exception {
		setGroups();

		String json = save(allEntities);

		deleteAll();

		assertEquals(0, groups.getEntityIds("group1").size());
		assertEquals(0, groups.getEntityIds("group2").size());

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		world.process();

		assertEquals(3, allEntities.getEntities().size());
		assertGroups();
	}

	public void test_serializer_save_load_with_groups_and_tags() throws Exception {
		setTags();
		setGroups();

		String json = save(allEntities);

		deleteAll();

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);
		world.process();

		assertEquals(3, allEntities.getEntities().size());

		assertTags();
		assertGroups();
	}

	public void test_save_load_entity_references() throws Exception {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityHolder holder = ee1.create(EntityHolder.class);
		holder.entity = world.getEntity(tags.getEntityId("tag1"));
		holder.entityId = world.getEntity(tags.getEntityId("tag3")).getId();

		tags.register("entity-holder", ee1.getEntityId());
		int entityHolderId = ee1.getEntityId();

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
		EntityHolder holder2 = entityHolder.getComponent(EntityHolder.class);
		assertNotEquals(entityHolder.getId(), entityHolderId);
		assertNotNull(holder2.entity);
		assertNotEquals(holder.entity, holder2.entity);
		assertNotEquals(holder.entityId, holder2.entityId);
	}

	public void test_save_load_bag_entity_references() throws Exception {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityBagHolder holder = ee1.create(EntityBagHolder.class);
		holder.entities.add(world.getEntity(tags.getEntityId("tag1")));
		holder.entities.add(world.getEntity(tags.getEntityId("tag3")));

		tags.register("entity-holder", ee1.getEntityId());
		int entityHolderId = ee1.getEntityId();

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
		EntityBagHolder holder2 = entityHolder.getComponent(EntityBagHolder.class);
		assertNotEquals(entityHolder.getId(), entityHolderId);
		assertNotNull(holder2.entities);
		assertEquals(2, holder2.entities.size());
		assertEquals(world.getEntity(tags.getEntityId("tag1")), holder2.entities.get(0));
		assertEquals(world.getEntity(tags.getEntityId("tag3")), holder2.entities.get(1));
	}

	private void assertNotEquals(int v, int v2) {
		assertTrue(v != v2);
	}

	private void assertNotEquals(Object v, Object v2) {
		assertTrue((v != v2) || (!v.equals(v2)));
	}

	public void test_save_load_intbag_entity_references() throws Exception {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityIntBagHolder holder = ee1.create(EntityIntBagHolder.class);
		holder.entities.add(world.getEntity(tags.getEntityId("tag1")).getId());
		holder.entities.add(world.getEntity(tags.getEntityId("tag3")).getId());

		tags.register("entity-holder", ee1.getEntityId());
		int entityHolderId = ee1.getEntityId();

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
		EntityIntBagHolder holder2 = entityHolder.getComponent(EntityIntBagHolder.class);
		assertNotEquals(entityHolder.getId(), entityHolderId);
		assertNotNull(holder2.entities);
		assertEquals(2, holder2.entities.size());
		assertEquals(world.getEntity(tags.getEntityId("tag1")), world.getEntity(holder2.entities.get(0)));
		assertEquals(world.getEntity(tags.getEntityId("tag3")), world.getEntity(holder2.entities.get(1)));
	}


	private void setTags() {
		IntBag entities = allEntities.getEntities();
		tags.register("tag1", world.getEntity(entities.get(0)));
		tags.register("tag3", world.getEntity(entities.get(2)));
	}

	private void assertTags() {
		IntBag entities = allEntities.getEntities();
		assertNotNull(entities.toString(), world.getEntity(tags.getEntityId("tag1")));
		assertNotNull(entities.toString(), world.getEntity(tags.getEntityId("tag3")));
	}

	private void assertGroups() {
		assertEquals(2, groups.getEntityIds("group1").size());
		assertEquals(1, groups.getEntityIds("group2").size());
	}

	private void setGroups() {
		IntBag entities = allEntities.getEntities();
		groups.add(world.getEntity(entities.get(0)), "group1");
		groups.add(world.getEntity(entities.get(0)), "group2");
		groups.add(world.getEntity(entities.get(2)), "group1");
	}

	private String save(EntitySubscription subscription)
			throws Exception {

		SaveFileFormat save = new SaveFileFormat(subscription.getEntities());
		ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
		manger.save(baos, save);
		return baos.toString(StandardCharsets.UTF_8);
	}

	private int deleteAll() {
		IntBag entities = allEntities.getEntities();
		int size = entities.size();
		for (int i = 0; size > i; i++) {
			world.delete(entities.get(i));
		}

		world.process();
		return size;
	}
}
