package com.artemis.io;

import com.artemis.Component;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Map;

public class KryoComponentLookupSerializer extends Serializer<SaveFileFormat.ComponentIdentifiers> {

	@Override
	public void write (Kryo kryo, Output output, SaveFileFormat.ComponentIdentifiers ci) {
		int count = 0;
		for (Map.Entry<Integer, Class<? extends Component>> entry : ci.idToType.entrySet()) {
			count++;
		}
		output.writeShort(count);
		for (Map.Entry<Integer, Class<? extends Component>> entry : ci.idToType.entrySet()) {
			output.writeShort(entry.getKey().intValue());
			output.writeString(entry.getValue().getName());
		}
	}

	@Override
	public SaveFileFormat.ComponentIdentifiers read (Kryo kryo, Input input,
		Class<SaveFileFormat.ComponentIdentifiers> aClass) {
		SaveFileFormat.ComponentIdentifiers ci = new SaveFileFormat.ComponentIdentifiers();
		int count = input.readShort();
		try {
			for (int i = 0; i < count; i++) {
				int index = input.readShort();
				ci.idToType.put(index, ClassReflection.forName(input.readString()));
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
		ci.build();
		return ci;
	}
}
