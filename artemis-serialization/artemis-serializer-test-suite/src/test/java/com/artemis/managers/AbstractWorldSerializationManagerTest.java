package com.artemis.managers;

import com.artemis.*;
import com.artemis.component.*;
import com.artemis.components.SerializationTag;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static com.artemis.utils.SerializationUtil.save;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Daan van Yperen
 */
public abstract class AbstractWorldSerializationManagerTest {

    protected WorldSerializationManager wsm;
    protected AspectSubscriptionManager subscriptions;
    protected TagManager tags;
    protected GroupManager groups;
    protected EntityWorld world;
    protected EntitySubscription allEntities;

    protected ComponentMapper<SerializationTag> serializationTagMapper;
    protected WorldSerializationManager.ArtemisSerializer<?> backend;

    @Before
    public void setup() {
        setupWorld();

        EntityEdit ee = world.edit(world.create());
        ee.create(ComponentX.class).text = "hello";
        ee.create(ComponentY.class).text = "whatever";
        ee.create(ReusedComponent.class);

        EntityEdit ee2 = world.edit(world.create());
        ee2.create(ComponentX.class).text = "hello 2";
        ee2.create(NameComponent.class).name = "do i work?";
        ee2.create(ComponentY.class).text = "whatever 2";
        ee2.create(ReusedComponent.class);

        EntityEdit ee3 = world.edit(world.create());
        ee3.create(ComponentX.class).text = "hello 3";
        ee3.create(ComponentY.class).text = "whatever 3";
        ee3.create(ReusedComponent.class);
        ee3.create(SerializationTag.class).tag = "whatever";

        world.process();
        assertEquals(3, allEntities.getEntities().size());
    }

    protected void setupWorld() {
        world = new EntityWorld(new WorldConfiguration()
                .setSystem(GroupManager.class)
                .setSystem(TagManager.class)
                .setSystem(WorldSerializationManager.class));

        world.inject(this);
        backend = createBackend(world);
        wsm.setSerializer(backend);

        allEntities = subscriptions.get(Aspect.all());
    }


    @Test
    public void serializer_save_load_std_format_new_world() throws Exception {
        byte[] save = save(allEntities);

        deleteAll();
        assertEquals(0, allEntities.getEntities().size());

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        setupWorld();

        allEntities = subscriptions.get(Aspect.all());
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);

        world.process();
        assertEquals(3, allEntities.getEntities().size());

        byte[] save2  = save(allEntities);

        deleteAll();
        assertEquals(0, allEntities.getEntities().size());
    }


    @Test
    public void serializer_save_to_file_and_load_std_format_new_world() throws Exception {
        byte[] save = save(allEntities);

        FileOutputStream fos = new FileOutputStream("save_temp");
        fos.write(save);
        fos.close();

        deleteAll();
        assertEquals(0, allEntities.getEntities().size());

        File file = new File("save_temp");
        InputStream is = new FileInputStream(file);
        setupWorld();

        allEntities = subscriptions.get(Aspect.all());

        SaveFileFormat load = wsm.load(is, SaveFileFormat.class);
        file.delete();

        world.process();
        assertEquals(3, allEntities.getEntities().size());

        byte[] save2 = save(allEntities);

        deleteAll();
        assertEquals(0, allEntities.getEntities().size());

    }

    @Test
    public void serializer_save_load_with_keys() throws Exception {
        setKeys();

        byte[] save = save(allEntities);

        deleteAll();

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);
        world.process();

        assertEquals(3, allEntities.getEntities().size());
        assertEquals(2, load.keys().size());
        assertKeys(load);
    }


    @Test
    public void serializer_save_load_with_tags() throws Exception {
        setTags();

        byte[] save = save(allEntities);

        deleteAll();

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);
        world.process();

        assertEquals(3, allEntities.getEntities().size());
        assertTags();
    }

    @Test
    public void serializer_save_load_with_groups() throws Exception {
        setGroups();

        byte[] save = save(allEntities);

        deleteAll();

        assertEquals(0, groups.getEntityIds("group1").size());
        assertEquals(0, groups.getEntityIds("group2").size());

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);
        world.process();

        assertEquals(3, allEntities.getEntities().size());
        assertGroups();
    }



    @Test
    public void serializer_save_load_with_groups_and_tags() throws Exception {
        setTags();
        setGroups();

        byte[] save = save(allEntities);

        deleteAll();

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);
        world.process();

        assertEquals(3, allEntities.getEntities().size());

        assertTags();
        assertGroups();
    }



    @Test
    public void serializer_save_load_with_groups_and_tags_and_keys() throws Exception {
        setKeys();
        setTags();
        setGroups();

        byte[] save = save(allEntities);

        deleteAll();

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);
        world.process();

        assertEquals(3, allEntities.getEntities().size());

        assertTags();
        assertGroups();
        assertKeys(load);
    }

    @Test
    public void save_load_entity_references() throws Exception {
        setTags();

        EntityEdit ee1 = world.createEntity().edit();
        EntityHolder holder = ee1.create(EntityHolder.class);
        holder.entity = world.getEntity(tags.getEntityId("tag1"));
        holder.entityId = world.getEntity(tags.getEntityId("tag3")).getId();

        tags.register("entity-holder", ee1.getEntityId());
        int entityHolderId = ee1.getEntityId();

        world.process();

        byte[] save = save(allEntities);

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);

        world.process();

        Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
        EntityHolder holder2 = entityHolder.getComponent(EntityHolder.class);
        assertNotEquals(entityHolder.getId(), entityHolderId);
        assertNotNull(holder2.entity);
        assertNotEquals(holder.entity, holder2.entity);
        assertNotEquals(holder.entityId, holder2.entityId);
    }




    @Test
    public void save_load_entity_reference_with_null() throws Exception {
        EntityEdit ee1 = world.createEntity().edit();
        EntityHolder holder = ee1.create(EntityHolder.class);
        holder.entity = null;
        holder.entityId = -1;

        EntityEdit ee2 = world.createEntity().edit();
        EntityHolder holder2 = ee2.create(EntityHolder.class);
        holder2.entity = world.getEntity(ee2.getEntityId());
        holder2.entityId = ee2.getEntityId();

        tags.register("ee1", ee1.getEntityId());
        int entityHolderId = ee1.getEntityId();

        tags.register("ee2", ee2.getEntityId());
        int entityHolderId2 = ee2.getEntityId();

        world.process();

        byte[] save = save(allEntities);

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);

        world.process();

        Entity entityHolder1 = world.getEntity(tags.getEntityId("ee1"));
        EntityHolder holder1b = entityHolder1.getComponent(EntityHolder.class);
        assertNotEquals(entityHolder1.getId(), entityHolderId);
        assertNull(holder1b.entity);
        assertEquals(-1, holder1b.entityId);

        Entity entityHolder2 = world.getEntity(tags.getEntityId("ee2"));
        EntityHolder holder2b = entityHolder2.getComponent(EntityHolder.class);
        assertNotEquals(entityHolder1.getId(), entityHolderId2);
        assertNotNull(holder2b.entity);
        assertNotEquals(holder.entity, holder2b.entity);
        assertNotEquals(holder.entityId, holder2b.entityId);
    }

    @Test
    public void save_load_bag_entity_references() throws Exception {
        setTags();

        EntityEdit ee1 = world.createEntity().edit();
        EntityBagHolder holder = ee1.create(EntityBagHolder.class);
        holder.entities.add(world.getEntity(tags.getEntityId("tag1")));
        holder.entities.add(world.getEntity(tags.getEntityId("tag3")));

        tags.register("entity-holder", ee1.getEntityId());
        int entityHolderId = ee1.getEntityId();

        world.process();

        byte[] save = save(allEntities);

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);

        world.process();

        Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
        EntityBagHolder holder2 = entityHolder.getComponent(EntityBagHolder.class);
        assertNotEquals(entityHolder.getId(), entityHolderId);
        assertNotNull(holder2.entities);
        assertEquals(2, holder2.entities.size());
        assertEquals(world.getEntity(tags.getEntityId("tag1")), holder2.entities.get(0));
        assertEquals(world.getEntity(tags.getEntityId("tag3")), holder2.entities.get(1));
    }

    @Test
    public void save_excludes_default_value_components() throws Exception {
        setTags();

        EntityEdit ee1 = world.createEntity().edit();
        ee1.create(ComponentX.class);
        ee1.create(ComponentY.class);
        ee1.create(ReusedComponent.class);
        ee1.create(SerializationTag.class).tag = "one";
        EntityEdit ee2 = world.createEntity().edit();
        ee2.create(ComponentX.class);
        ee2.create(ComponentY.class).text = "only me";
        ee2.create(ReusedComponent.class);
        ee2.create(SerializationTag.class).tag = "two";

        world.process();

        byte[] save = save(allEntities);

        deleteAll();

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat l = wsm.load(bais, SaveFileFormat.class);

        world.process();

        assertEquals(5, allEntities.getEntities().size());
        assertEquals(world.getEntity(tags.getEntityId("tag3")).getCompositionId(), l.get("one").getCompositionId());
        assertEquals(l.get("one").getCompositionId(), l.get("two").getCompositionId());
    }

    @Test
    public void save_load_intbag_entity_references() throws Exception {
        setTags();

        EntityEdit ee1 = world.createEntity().edit();
        EntityIntBagHolder holder = ee1.create(EntityIntBagHolder.class);
        holder.entities.add(world.getEntity(tags.getEntityId("tag1")).getId());
        holder.entities.add(world.getEntity(tags.getEntityId("tag3")).getId());

        tags.register("entity-holder", ee1.getEntityId());
        int entityHolderId = ee1.getEntityId();

        world.process();

        byte[] save = save(allEntities);

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat l = wsm.load(bais, SaveFileFormat.class);

        world.process();

        Entity entityHolder = world.getEntity(tags.getEntityId("entity-holder"));
        EntityIntBagHolder holder2 = entityHolder.getComponent(EntityIntBagHolder.class);
        assertNotEquals(entityHolder.getId(), entityHolderId);
        assertNotNull(holder2.entities);
        assertEquals(2, holder2.entities.size());
        assertEquals(world.getEntity(tags.getEntityId("tag1")), world.getEntity(holder2.entities.get(0)));
        assertEquals(world.getEntity(tags.getEntityId("tag3")), world.getEntity(holder2.entities.get(1)));
    }

    @Test
    public void loaded_entities_id_order_matches_json_layout() throws Exception {
        byte[] save = save(allEntities);

        world.delete(2);
        world.process();

        deleteAll();

        assertEquals(0, allEntities.getEntities().size());

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat load = wsm.load(bais, SaveFileFormat.class);;
        world.process();

        assertEquals(3, allEntities.getEntities().size());

        // this would be out of order in 1.2.1 and earlier, due
        // to entityId 2 being deleted before 0 and 1.
        IntBag sorted = new IntBag();
        sorted.addAll(load.entities);
        Arrays.sort(sorted.getData(), 0, sorted.size());
        assertEquals(sorted, load.entities);
    }

    @Test(expected = RuntimeException.class)
    public void serializer_save_not_entity_bag_fail() throws Exception {
        setupWorld();

        EntityEdit ee = world.createEntity().edit();
        NotEntityBagHolder holder = ee.create(NotEntityBagHolder.class);
        holder.strings.add("s1");
        world.process();

        byte[] save = save(allEntities);

        ByteArrayInputStream bais = new ByteArrayInputStream(save);
        SaveFileFormat l = wsm.load(bais, SaveFileFormat.class);

        world.process();
        Assert.fail("Should have failed");
    }

    @Test
    public void implicitly_saved_entities_includes_archetypes() {
        setupWorld();

        EntityEdit ee = world.createEntity().edit();
        ee.create(Size.class);
        int id1 = ee.getEntityId();

        EntityEdit ee2 = world.createEntity().edit();
        ee2.create(EntityHolder.class).entityId = id1;
        int id2 = ee2.getEntityId();

        world.process();

        IntBag toSave = new IntBag();
        toSave.add(id2);
        SaveFileFormat save = new SaveFileFormat(toSave);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        wsm.save(baos, save);

        // only saving 2nd entity, need to make sure the archetype for
        // id1 is pulled in too
        assertEquals(2, save.archetypes.compositionIdMapper.size());
    }

    private void setTags() {
        IntBag entities = allEntities.getEntities();
        tags.register("tag1", world.getEntity(entities.get(0)));
        tags.register("tag3", world.getEntity(entities.get(2)));
    }

    private void setKeys() {
        IntBag entities = allEntities.getEntities();
        serializationTagMapper.create(entities.get(0)).tag = "key1";
        serializationTagMapper.create(entities.get(2)).tag = "key3";
    }

    private void assertTags() {
        IntBag entities = allEntities.getEntities();
        assertNotNull(entities.toString(), world.getEntity(tags.getEntityId("tag1")));
        assertNotNull(entities.toString(), world.getEntity(tags.getEntityId("tag3")));
    }

    private void assertKeys(SaveFileFormat loaded) {
        IntBag entities = allEntities.getEntities();
        assertNotNull(entities.toString(), loaded.get("key1"));
        assertNotNull(entities.toString(), loaded.get("key3"));
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

    private int deleteAll() {
        IntBag entities = allEntities.getEntities();
        int size = entities.size();
        for (int i = 0; size > i; i++) {
            world.delete(entities.get(i));
        }

        world.process();
        return size;
    }


    private byte[] save(EntitySubscription subscription) throws Exception {
        return save(subscription.getEntities());
    }

    protected byte[] save(IntBag entities) throws Exception {
        SaveFileFormat save = new SaveFileFormat(entities);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        wsm.save(baos, save);
        return baos.toByteArray();
    }

    protected abstract WorldSerializationManager.ArtemisSerializer<?> createBackend(World world);
}
