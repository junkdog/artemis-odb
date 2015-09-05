package com.artemis.io;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.IdentityHashMap;
import java.util.Map;

public class ComponentLookupSerializer implements Json.Serializer<IdentityHashMap> {
	private World world;
	private IdentityHashMap<Class<? extends Component>, String> componentMap;

	public ComponentLookupSerializer(World world) {
		this.world = world;
	}

	public void setComponentMap(IdentityHashMap<Class<? extends Component>, String> componentMap) {
		this.componentMap = componentMap;
	}

	public IdentityHashMap<Class<? extends Component>, String> getComponentMap() {
		return componentMap;
	}

	@Override
	public void write(Json json, IdentityHashMap object, Class knownType) {
		json.writeObjectStart();
		for (Map.Entry<Class<? extends Component>, String> entry : componentMap.entrySet()) {
			json.writeValue(entry.getKey().getName(), entry.getValue());
		}
		json.writeObjectEnd();
	}

	@Override
	public IdentityHashMap read(Json json, JsonValue jsonData, Class type) {
		IdentityHashMap<Class<? extends Component>, String> componentMap
				= new IdentityHashMap<Class<? extends Component>, String>();

		JsonValue component = jsonData.child;
		try {
			while (component != null) {
				Class c = ClassReflection.forName(component.name());
				componentMap.put(c, component.asString());
				component = component.next;
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}

		return componentMap;
	}
}
