package com.artemis.managers;

import com.artemis.*;
import com.artemis.component.LevelState;
import com.artemis.component.ParentedPosition;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.KryoArtemisSerializer;
import com.artemis.io.KryoEntitySerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class KryoEntityReferencesTest {
	private World world;
	private WorldSerializationManager manger;
	private TagManager tags;

	private ComponentMapper<ParentedPosition> parentedPositionMapper;
	private ComponentMapper<LevelState> levelStateMapper;

	@Test
	public void load_before_save() throws Exception {
		SaveFileFormat load = loadWorld();

		Entity base = tags.getEntity("level");
		Entity star1 = tags.getEntity("star1");

		assertEquals(5, load.entities.size());

		assertNotNull(base);
		assertNotNull(star1);

		assertEquals(base.getId(), parentedPositionMapper.get(star1).origin);

		LevelState state = levelStateMapper.get(base);
		assertEquals(star1.getId(), state.starId1);
	}

		@Test
	public void load_entity_with_references() throws Exception {
		SaveFileFormat load = loadWorld();

		Entity base = tags.getEntity("level");
		Entity star1 = tags.getEntity("star1");
		Entity star2 = tags.getEntity("star2");
		Entity star3 = tags.getEntity("star3");
		Entity shadow = tags.getEntity("shadow");

		assertEquals(5, load.entities.size());

		assertNotNull(base);
		assertNotNull(star1);
		assertNotNull(star2);
		assertNotNull(star3);
		assertNotNull(shadow);

		assertEquals(base.getId(), parentedPositionMapper.get(star1).origin);
		assertEquals(base.getId(), parentedPositionMapper.get(star2).origin);
		assertEquals(base.getId(), parentedPositionMapper.get(star3).origin);

		LevelState state = levelStateMapper.get(base);
		assertEquals(star1.getId(), state.starId1);
		assertEquals(star2.getId(), state.starId2);
		assertEquals(star3.getId(), state.starId3);
	}

	private SaveFileFormat loadWorld() {
		world = new World(new WorldConfiguration()
				.setSystem(TagManager.class)
				.setSystem(WorldSerializationManager.class));

		world.inject(this);

		// TODO this is super janky
		JsonArtemisSerializer backend2 = new JsonArtemisSerializer(world);
		manger.setSerializer(backend2);
		InputStream is = KryoEntityReferencesTest.class.getResourceAsStream("/level_3.json");
		SaveFileFormat load = manger.load(is, SaveFileFormat.class);
		world.process();

		KryoArtemisSerializer backend = new KryoArtemisSerializer(world);
		world.inject(backend);
		manger.setSerializer(backend);

		try {
			// todo this doenst really work
			File root = new File(KryoEntityReferencesTest.class.getResource("/level_3.json").getFile()).getParentFile();
			// this is actually in target/test-classes for whatever reason
			File file = new File(root, "/level_3.bin");
			FileOutputStream fos = new FileOutputStream(file);
			backend.saveBinary(fos, load);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		deleteAll();
		try {
			is = KryoEntityReferencesTest.class.getResource("/level_3.bin").openStream();
			// using binary is more flexible, but we don't have api for that in manager
			load = backend.loadBinary(is, SaveFileFormat.class);
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
