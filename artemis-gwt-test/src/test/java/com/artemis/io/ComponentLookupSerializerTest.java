package com.artemis.io;

import com.artemis.Component;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ReusedComponent;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.OutputType;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.IdentityHashMap;

/**
 * @author Daan van Yperen
 */
public class ComponentLookupSerializerTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_read_write_read_components() {
		World world = new World();
		EntityEdit ee = world.createEntity().edit();
		ee.create(ComponentX.class);
		ee.create(ComponentY.class);
		ee.create(ReusedComponent.class);

		world.process();

		Json json = new Json(OutputType.javascript);
		ComponentLookupSerializer serializer = new ComponentLookupSerializer(world);
		IdentityHashMap<Class<? extends Component>, String> componentMap = serializer.classToIdentifierMap();
		json.setSerializer(IdentityHashMap.class, new ComponentLookupSerializer(world));

		String serialized = json.toJson(componentMap);
		IdentityHashMap map = json.fromJson(IdentityHashMap.class, serialized);

		assertArrayEquals(componentMap.values().toArray(), map.values().toArray());
		assertEquals(componentMap.keySet(), map.keySet());
	}

	/** Assert array equals for poor people. */
	protected void assertArrayEquals(Object[] a1, Object[] a2) {
		assertEquals(a1.length, a2.length);
		int index=0;
		for (Object o1 : a1) {
			final Object o2 = a2[index++];
			assertEquals(o1, o2);
		}
	}

}
