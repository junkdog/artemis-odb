package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.*;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@Wire
public class JsonWorldSerializationManagerTest {
	private WorldSerializationManager manger;
	private AspectSubscriptionManager subscriptions;
	private TagManager tags;
	private GroupManager groups;
	private World world;
	private EntitySubscription allEntities;

	@Before
	public void setup() {
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
		world = new World(new WorldConfiguration()
				.setManager(GroupManager.class)
				.setManager(TagManager.class)
				.setManager(WorldSerializationManager.class));

		world.inject(this);
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		manger.setSerializer(backend);
	}

	@Test
	public void serializer_save_load_std_format_new_world() {
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

	@Test
	public void serializer_save_to_file_and_load_std_format_new_world() throws Exception {
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

	}

	@Test
	public void serializer_save_load_with_tags() {
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

	@Test
	public void serializer_save_load_with_groups() {
		setGroups();

		String json = save(allEntities);

		deleteAll();

		assertEquals(0, groups.getEntities("group1").size());
		assertEquals(0, groups.getEntities("group2").size());

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		world.process();

		assertEquals(3, allEntities.getEntities().size());
		assertGroups();
	}

	@Test
	public void serializer_save_load_with_groups_and_tags() {
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

	@Test
	public void save_load_entity_references() {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityHolder holder = ee1.create(EntityHolder.class);
		holder.entity = tags.getEntity("tag1");
		holder.entityId = tags.getEntity("tag3").id;

		tags.register("entity-holder", ee1.getEntity());
		int entityHolderId = ee1.getEntity().id;

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = tags.getEntity("entity-holder");
		EntityHolder holder2 = entityHolder.getComponent(EntityHolder.class);
		assertNotEquals(entityHolder.id, entityHolderId);
		assertNotNull(holder2.entity);
		assertNotEquals(holder.entity, holder2.entity);
		assertNotEquals(holder.entityId, holder2.entityId);
	}

	@Test
	public void save_load_bag_entity_references() {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityBagHolder holder = ee1.create(EntityBagHolder.class);
		holder.entities.add(tags.getEntity("tag1"));
		holder.entities.add(tags.getEntity("tag3"));

		tags.register("entity-holder", ee1.getEntity());
		int entityHolderId = ee1.getEntity().id;

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = tags.getEntity("entity-holder");
		EntityBagHolder holder2 = entityHolder.getComponent(EntityBagHolder.class);
		assertNotEquals(entityHolder.id, entityHolderId);
		assertNotNull(holder2.entities);
		assertEquals(2, holder2.entities.size());
		assertEquals(tags.getEntity("tag1"), holder2.entities.get(0));
		assertEquals(tags.getEntity("tag3"), holder2.entities.get(1));
	}

	@Test
	public void save_load_intbag_entity_references() {
		setTags();

		EntityEdit ee1 = world.createEntity().edit();
		EntityIntBagHolder holder = ee1.create(EntityIntBagHolder.class);
		holder.entities.add(tags.getEntity("tag1").id);
		holder.entities.add(tags.getEntity("tag3").id);

		tags.register("entity-holder", ee1.getEntity());
		int entityHolderId = ee1.getEntity().id;

		world.process();

		String json = save(allEntities);

		ByteArrayInputStream is = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		manger.load(is, SaveFileFormat.class);

		world.process();

		Entity entityHolder = tags.getEntity("entity-holder");
		EntityIntBagHolder holder2 = entityHolder.getComponent(EntityIntBagHolder.class);
		assertNotEquals(entityHolder.id, entityHolderId);
		assertNotNull(holder2.entities);
		assertEquals(2, holder2.entities.size());
		assertEquals(tags.getEntity("tag1"), world.getEntity(holder2.entities.get(0)));
		assertEquals(tags.getEntity("tag3"), world.getEntity(holder2.entities.get(1)));
	}


	private void setTags() {
		IntBag entities = allEntities.getEntities();
		tags.register("tag1", world.getEntity(entities.get(0)));
		tags.register("tag3", world.getEntity(entities.get(2)));
	}

	private void assertTags() {
		IntBag entities = allEntities.getEntities();
		assertNotNull(entities.toString(), tags.getEntity("tag1"));
		assertNotNull(entities.toString(), tags.getEntity("tag3"));
	}

	private void assertGroups() {
		assertEquals(2, groups.getEntities("group1").size());
		assertEquals(1, groups.getEntities("group2").size());
	}

	private void setGroups() {
		IntBag entities = allEntities.getEntities();
		groups.add(world.getEntity(entities.get(0)), "group1");
		groups.add(world.getEntity(entities.get(0)), "group2");
		groups.add(world.getEntity(entities.get(2)), "group1");
	}

	private String save(EntitySubscription subscription) {
		StringWriter writer = new StringWriter();
		SaveFileFormat save = new SaveFileFormat(subscription.getEntities());
		manger.save(writer, save);
		return writer.toString();
	}

	private int deleteAll() {
		IntBag entities = allEntities.getEntities();
		int size = entities.size();
		for (int i = 0; size > i; i++) {
			world.deleteEntity(entities.get(i));
		}

		world.process();
		return size;
	}
}
