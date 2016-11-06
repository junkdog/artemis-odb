package com.artemis.prefab;


import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.component.ComponentX;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.WorldSerializationManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrefabTest {
	private World world;
	private WorldSerializationManager manager;

	@Before
	public void setup() {
		world = new World(new WorldConfiguration()
			.setSystem(GroupManager.class)
			.setSystem(TagManager.class)
			.setSystem(WorldSerializationManager.class));

		world.inject(this);
		JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
		backend.prettyPrint(true);
		manager.setSerializer(backend);

	}

	@Test
	public void load_prefab() throws Exception {
		SomePrefab prefab = new SomePrefab(world, new JsonValuePrefabReader());
		String text = "updating whatever's ComponentX";
		SaveFileFormat l = prefab.create(text);
		assertEquals(3, l.entities.size());
		assertEquals(text, l.get("whatever").getComponent(ComponentX.class).text);

		world.process();
	}

	@Test(expected = MissingPrefabDataException.class)
	public void missing_annotation() {
		new NoAnnotationPrefab(world, new JsonValuePrefabReader());
	}
}
