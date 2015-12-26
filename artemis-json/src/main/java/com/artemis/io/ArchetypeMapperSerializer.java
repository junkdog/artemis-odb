package com.artemis.io;

import com.artemis.World;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

import java.util.Map;

public class ArchetypeMapperSerializer implements JsonSerializer<ArchetypeMapper> {
	@Override
	public void write(Json json, ArchetypeMapper object, Class knownType) {
		json.writeObjectStart();
		for (Map.Entry<Integer, ArchetypeMapper.TransmuterEntry> entry : object.entrySet()) {
			json.writeValue(entry.getKey().toString(), entry.getValue());
		}
		json.writeObjectEnd();
	}

	@Override
	public ArchetypeMapper read(Json json, JsonValue jsonData, Class type) {
		JsonValue entry = jsonData.child;

		ArchetypeMapper archetypes = new ArchetypeMapper();
		while (entry != null) { // out array object
			ArchetypeMapper.TransmuterEntry te =
				json.readValue(ArchetypeMapper.TransmuterEntry.class, entry);

			archetypes.compositionIdMapper.put(Integer.valueOf(entry.name), te);
			entry = entry.next;
		}

		return archetypes;
	}
}
