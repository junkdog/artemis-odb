package com.artemis.io;

import com.artemis.Component;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

import java.util.Map;

public class ComponentLookupSerializer implements JsonSerializer<SaveFileFormat.ComponentIdentifiers> {

	@Override
	public void write(Json json, SaveFileFormat.ComponentIdentifiers ci, Class knownType) {
		json.writeObjectStart();
		for (Map.Entry<Class<? extends Component>, String> entry : ci.typeToName.entrySet()) {
			json.writeValue(entry.getKey().getName(), entry.getValue());
		}
		json.writeObjectEnd();
	}

	@Override
	public SaveFileFormat.ComponentIdentifiers read(Json json, JsonValue jsonData, Class type) {
		SaveFileFormat.ComponentIdentifiers ci = new SaveFileFormat.ComponentIdentifiers();

		JsonValue component = jsonData.child;
		try {
			while (component != null) {
				Class c = ClassReflection.forName(component.name());
				ci.typeToName.put(c, component.asString());
				component = component.next;
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}

		ci.build();
		return ci;
	}
}
