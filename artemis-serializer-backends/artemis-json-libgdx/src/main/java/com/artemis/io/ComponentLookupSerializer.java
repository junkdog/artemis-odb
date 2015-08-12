package com.artemis.io;

import com.artemis.Component;
import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.Transient;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.IdentityHashMap;
import java.util.Map;

public class ComponentLookupSerializer implements Json.Serializer<IdentityHashMap> {
	private World world;

	public ComponentLookupSerializer(World world) {
		this.world = world;
	}

	@Override
	public void write(Json json, IdentityHashMap object, Class knownType) {
		IdentityHashMap<Class<? extends Component>, String> componentMap = classToIdentifierMap();

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

	protected IdentityHashMap<Class<? extends Component>, String> classToIdentifierMap() {
		IdentityHashMap<Class<? extends Component>, String> components =
				new IdentityHashMap<Class<? extends Component>, String>();

		ImmutableBag<ComponentType> types = world.getComponentManager().getComponentTypes();
		for (int i = 0; i < types.size(); i++) {
			Class<? extends Component> type = types.get(i).getType();
			if (ClassReflection.getDeclaredAnnotation(type, Transient.class) != null)
				continue;

			components.put(type, i + "_" + type.getSimpleName());
		}

		return components;
	}
}
