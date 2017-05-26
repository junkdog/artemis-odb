package com.artemis.managers;

import com.artemis.*;
import com.artemis.component.*;
import com.artemis.component.render.TextureReference;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.KryoArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.artemis.utils.Vector2;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class KryoEntityReferencesTest {
	private World world;
	private WorldSerializationManager manger;
	private TagManager tags;

	private ComponentMapper<ParentedPosition> parentedPositionMapper;
	private ComponentMapper<LevelState> levelStateMapper;

	@Test
	public void load_before_save() throws Exception {
		SaveFileFormat load = loadWorld();

		int base = tags.getEntityId("level");
		int star1 = tags.getEntityId("star1");

		assertEquals(5, load.entities.size());

		assertNotEquals(-1, base);
		assertNotEquals(-1,star1);

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

			assertNotEquals(-1,base);
			assertNotEquals(-1,star1);
			assertNotEquals(-1,star2);
			assertNotEquals(-1,star3);
			assertNotEquals(-1,shadow);

		assertEquals(base, parentedPositionMapper.get(star1).origin);
		assertEquals(base, parentedPositionMapper.get(star2).origin);
		assertEquals(base, parentedPositionMapper.get(star3).origin);

		LevelState state = levelStateMapper.get(base);
		assertEquals(star1, state.starId1);
		assertEquals(star2, state.starId2);
		assertEquals(star3, state.starId3);
	}

	private SaveFileFormat loadWorld() {
		world = new EntityWorld(new WorldConfiguration()
				.setSystem(TagManager.class)
				.setSystem(WorldSerializationManager.class));

		world.inject(this);

		JsonArtemisSerializer backend2 = new JsonArtemisSerializer(world);
		manger.setSerializer(backend2);
		InputStream is = KryoEntityReferencesTest.class.getResourceAsStream("/level_3.json");
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		world.process();

		KryoArtemisSerializer backend = new KryoArtemisSerializer(world);
		world.inject(backend);
		manger.setSerializer(backend);

		try {
			File root = new File(KryoEntityReferencesTest.class.getResource("/level_3.json").getFile()).getParentFile();
			// this is actually in target/test-classes for whatever reason
			File file = new File(root, "/level_3.bin");
			FileOutputStream fos = new FileOutputStream(file);
			backend.save(fos, load);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		deleteAll();
		try {
			is = KryoEntityReferencesTest.class.getResource("/level_3.bin").openStream();
			// using binary is more flexible, but we don't have api for that in manager
			load = backend.load(is, SaveFileFormat.class);
		} catch (FileNotFoundException e) {
			throw new AssertionError(e);
		} catch (IOException e) {
			throw new AssertionError(e);
		}

		world.process();

		return load;
	}

	private int deleteAll() {
		IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
		int size = entities.size();
		for (int i = 0; size > i; i++) {
			world.delete(entities.get(i));
		}

		world.process();
		return size;
	}
}
