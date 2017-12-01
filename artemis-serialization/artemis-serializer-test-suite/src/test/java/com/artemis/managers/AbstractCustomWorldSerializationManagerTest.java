package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.SkipWire;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * @author Daan van Yperen
 */
public abstract class AbstractCustomWorldSerializationManagerTest {
    protected WorldSerializationManager manger;
    protected AspectSubscriptionManager subscriptions;
    protected SerializedSystem serializedSystem;
    @SkipWire
    protected World world;
    protected EntitySubscription allEntities;

    protected abstract WorldSerializationManager.ArtemisSerializer<?> createBackend(World world);

    @Before
    public void setup() {
        world = new World(new WorldConfiguration()
                .setSystem(SerializedSystem.class)
                .setSystem(WorldSerializationManager.class));

        world.inject(this);
        manger.setSerializer(createBackend(world));

        allEntities = subscriptions.get(Aspect.all());

        world.process();
        assertEquals(0, allEntities.getEntities().size());
    }

    @Test
    public void custom_save_format_save_load() throws Exception {
        serializedSystem.serializeMe = "dog";

        byte[] json = save(allEntities, "a string", 420);
        serializedSystem.serializeMe = "cat";

        deleteAll();
        assertEquals(0, allEntities.getEntities().size());

        ByteArrayInputStream is = new ByteArrayInputStream(
                json);
        CustomSaveFormat load = manger.load(is, CustomSaveFormat.class);

        world.process();
        assertEquals("DOG", serializedSystem.serializeMe);
        assertEquals("DOG", load.serialized.serializeMe);
        assertEquals("a string", load.noSerializer.text);
        assertEquals(420, load.noSerializer.number);
    }

    private byte[] save(EntitySubscription subscription, String s, int i)
            throws Exception {

        SaveFileFormat save = new CustomSaveFormat(subscription, s, i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        manger.save(baos, save);
        return baos.toByteArray();
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

    public static class SerializedSystem extends BaseSystem {
        public String serializeMe;

        @Override
        protected void processSystem() {}
    }

    public static class DummySegment {
        public String text;
        public int number;

        public DummySegment(String text, int number) {
            this.text = text;
            this.number = number;
        }

        public DummySegment() {}
    }
}
