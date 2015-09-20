package com.artemis.managers;

import com.artemis.ComponentMapper;
import com.artemis.EntityHelper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.annotations.Wire;
import com.artemis.component.LevelState;
import com.artemis.component.ParentedPosition;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityReferencesTest {
	private World world;
	private WorldSerializationManager manger;
	private TagManager tags;

	private ComponentMapper<ParentedPosition> parentedPositionMapper;
	private ComponentMapper<LevelState> levelStateMapper;

	@Test
	public void load_before_save() throws Exception {
		SaveFileFormat load = loadWorld();

		int base = tags.getEntity("level");
		int star1 = tags.getEntity("star1");

		assertEquals(5, load.entities.size());

		assertNotNull(base);
		assertNotNull(star1);

		assertEquals(base, parentedPositionMapper.get(star1).origin);

		LevelState state = levelStateMapper.get(base);
		assertEquals(star1, state.starId1);
	}

		@Test
	public void load_entity_with_references() throws Exception {
		SaveFileFormat load = loadWorld();

		int base = tags.getEntity("level");
		int star1 = tags.getEntity("star1");
		int star2 = tags.getEntity("star2");
		int star3 = tags.getEntity("star3");
		int shadow = tags.getEntity("shadow");

		assertEquals(5, load.entities.size());

		assertNotNull(base);
		assertNotNull(star1);
		assertNotNull(star2);
		assertNotNull(star3);
		assertNotNull(shadow);

		assertEquals(base, parentedPositionMapper.get(star1).origin);
		assertEquals(base, parentedPositionMapper.get(star2).origin);
		assertEquals(base, parentedPositionMapper.get(star3).origin);

		LevelState state = levelStateMapper.get(base);
		assertEquals(star1, state.starId1);
		assertEquals(star2, state.starId2);
		assertEquals(star3, state.starId3);
	}

	private SaveFileFormat loadWorld() {
		world = new World(new WorldConfiguration()
				.setSystem(TagManager.class)
				.setSystem(WorldSerializationManager.class));

		world.inject(this);
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		manger.setSerializer(backend);

		InputStream is = EntityReferencesTest.class.getResourceAsStream("/level_3.json");
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);

		world.process();

		return load;
	}
}
