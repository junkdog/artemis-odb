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
		for (Map.Entry<Class<? extends Component>, String> entry : ci.typeToName.entrySet()) {
			count++;
		}
		output.writeInt(count);
		for (Map.Entry<Class<? extends Component>, String> entry : ci.typeToName.entrySet()) {
			output.writeString(entry.getKey().getName());
			output.writeString(entry.getValue());
		}
	}

	@Override
	public SaveFileFormat.ComponentIdentifiers read (Kryo kryo, Input input,
		Class<SaveFileFormat.ComponentIdentifiers> aClass) {
		SaveFileFormat.ComponentIdentifiers ci = new SaveFileFormat.ComponentIdentifiers();
		int count = input.readInt();
		try {
			for (int i = 0; i < count; i++) {
				Class c = ClassReflection.forName(input.readString());
				ci.typeToName.put(c, input.readString());
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
		ci.build();
		return ci;
	}
}
