package com.artemis.io;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

public class TransmuterEntrySerializer implements JsonSerializer<ArchetypeMapper.TransmuterEntry> {
	@Override
	public void write(Json json, ArchetypeMapper.TransmuterEntry object, Class knownType) {
		json.writeArrayStart();
		for (int i = 0; i < object.componentTypes.size(); i++) {
			json.writeValue(object.componentTypes.get(i).getName());
		}

		json.writeArrayEnd();
	}

	@Override
	public ArchetypeMapper.TransmuterEntry read(Json json, JsonValue jsonData, Class type) {
		Bag components = new Bag();
		for (JsonValue child = jsonData.child; child != null; child = child.next)
			components.add(toClass(json.readValue(String.class, child)));

		return new ArchetypeMapper.TransmuterEntry(components);
	}

	private Class<? extends Component> toClass(String klazz) {
		try {
			return ClassReflection.forName(klazz);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
