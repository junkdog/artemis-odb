package com.artemis.io;

import com.artemis.Component;
import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.Transient;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
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
//		json.writeObjectStart("components");
		for (Map.Entry<Class<? extends Component>, String> entry : componentMap.entrySet()) {
			json.writeValue(entry.getKey().getName(), entry.getValue());
		}
//		json.writeObjectEnd();
		json.writeObjectEnd();
	}

	@Override
	public IdentityHashMap read(Json json, JsonValue jsonData, Class type) {
		IdentityHashMap<Class<? extends Component>, String> componentMap
				= new IdentityHashMap<Class<? extends Component>, String>();

//		JsonValue component = jsonData.getChild("components");
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
			if (type.getAnnotation(Transient.class) != null)
				continue;

			components.put(type, i + "_" + type.getSimpleName());
		}

		return components;
	}

	protected Map<String, Class<? extends Component>> identifierToClassMap() {
		Map<String, Class<? extends Component>> components =
				new HashMap<String, Class<? extends Component>>();

		for (Map.Entry<Class<? extends Component>, String> entry : classToIdentifierMap().entrySet()) {
			components.put(entry.getValue(), entry.getKey());
		}

		return components;
	}
}
