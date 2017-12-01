package com.artemis.managers;

import com.artemis.ComponentMapper;
import com.artemis.EntityWorld;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.component.LevelState;
import com.artemis.component.ParentedPosition;
import com.artemis.io.SaveFileFormat;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Daan van Yperen
 */
public abstract class AbstractEntityReferencesTest {
    protected World world;
    protected WorldSerializationManager manger;
    protected TagManager tags;
    protected ComponentMapper<ParentedPosition> parentedPositionMapper;
    protected ComponentMapper<LevelState> levelStateMapper;
    private String filename;

    public AbstractEntityReferencesTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void load_before_save() throws Exception {
        SaveFileFormat load = loadWorld();

        int base = tags.getEntityId("level");
        int star1 = tags.getEntityId("star1");

        assertEquals(5, load.entities.size());

        assertNotEquals(-1, base);
        assertNotEquals(-1, star1);

        assertEquals(base, parentedPositionMapper.get(star1).origin);

        LevelState state = levelStateMapper.get(base);
        assertEquals(star1, state.starId1);
    }

    @Test
    public void load_entity_with_references() throws Exception {
        SaveFileFormat load = loadWorld();

        int base = tags.getEntityId("level");
        int star1 = tags.getEntityId("star1");
        int star2 = tags.getEntityId("star2");
        int star3 = tags.getEntityId("star3");
        int shadow = tags.getEntityId("shadow");

        assertEquals(5, load.entities.size());

        assertNotEquals(-1, base);
        assertNotEquals(-1, star1);
        assertNotEquals(-1, star2);
        assertNotEquals(-1, star3);
        assertNotEquals(-1, shadow);

        assertEquals(base, parentedPositionMapper.get(star1).origin);
        assertEquals(base, parentedPositionMapper.get(star2).origin);
        assertEquals(base, parentedPositionMapper.get(star3).origin);

        LevelState state = levelStateMapper.get(base);
        assertEquals(star1, state.starId1);
        assertEquals(star2, state.starId2);
        assertEquals(star3, state.starId3);
    }

    protected SaveFileFormat loadWorld() {
        world = new EntityWorld(new WorldConfiguration()
                .setSystem(TagManager.class)
                .setSystem(WorldSerializationManager.class));
        world.inject(this);
        manger.setSerializer(createBackend(world));
        InputStream is = AbstractEntityReferencesTest.class.getResourceAsStream(filename);
        SaveFileFormat load = manger.load(is, SaveFileFormat.class);
        world.process();
        return load;
    }

    protected abstract WorldSerializationManager.ArtemisSerializer<?> createBackend(World world);
}
