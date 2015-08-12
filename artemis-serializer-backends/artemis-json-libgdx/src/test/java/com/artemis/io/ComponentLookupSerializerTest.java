package com.artemis.io;

import com.artemis.Component;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.junit.Test;

import java.util.IdentityHashMap;

import static org.junit.Assert.*;

public class ComponentLookupSerializerTest {

	@Test
	public void read_write_read_components() {
		World world = new World();
		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class);
		ee.create(ComponentY.class);
		ee.create(ReusedComponent.class);

		world.process();

		Json json = new Json(JsonWriter.OutputType.javascript);
		ComponentLookupSerializer serializer = new ComponentLookupSerializer(world);
		IdentityHashMap<Class<? extends Component>, String> componentMap = serializer.classToIdentifierMap();
		json.setSerializer(IdentityHashMap.class, new ComponentLookupSerializer(world));

		String serialized = json.toJson(componentMap);
		IdentityHashMap map = json.fromJson(IdentityHashMap.class, serialized);

		assertArrayEquals(componentMap.values().toArray(), map.values().toArray());
		assertEquals(componentMap.keySet(), map.keySet());
	}
}