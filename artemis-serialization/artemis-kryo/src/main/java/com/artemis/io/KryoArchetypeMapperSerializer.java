package com.artemis.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Map;

public class KryoArchetypeMapperSerializer extends Serializer<ArchetypeMapper> {

	@Override
	public void write (Kryo kryo, Output output, ArchetypeMapper archetypeMapper) {
		int count = 0;
		for (Map.Entry<Integer, ArchetypeMapper.TransmuterEntry> entry : archetypeMapper.entrySet()) {
			count++;
		}
		output.writeInt(count);
		for (Map.Entry<Integer, ArchetypeMapper.TransmuterEntry> entry : archetypeMapper.entrySet()) {
			output.writeInt(entry.getKey());
			kryo.writeObject(output, entry.getValue());
		}
	}

	@Override
	public ArchetypeMapper read (Kryo kryo, Input input, Class<ArchetypeMapper> aClass) {
		ArchetypeMapper archetypes = new ArchetypeMapper();
		int count = input.readInt();
		for (int i = 0; i < count; i++) {
			int id = input.readInt();
			ArchetypeMapper.TransmuterEntry te = kryo.readObject(input, ArchetypeMapper.TransmuterEntry.class);
			archetypes.compositionIdMapper.put(id, te);
		}
		return archetypes;
	}
}
