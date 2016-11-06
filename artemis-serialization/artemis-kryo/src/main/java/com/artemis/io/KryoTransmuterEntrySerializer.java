package com.artemis.io;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTransmuterEntrySerializer extends Serializer<ArchetypeMapper.TransmuterEntry> {
	SaveFileFormat.ComponentIdentifiers identifiers;

	@Override
	public void write (Kryo kryo, Output output, ArchetypeMapper.TransmuterEntry te) {
		int count = te.componentTypes.size();
		output.writeInt(count);
		for (int i = 0; i < count; i++) {
			Class<? extends Component> type = te.componentTypes.get(i);
			int index = identifiers.typeToId.get(type);
			output.writeShort(index);
		}
	}

	@Override
	public ArchetypeMapper.TransmuterEntry read (Kryo kryo, Input input, Class<ArchetypeMapper.TransmuterEntry> aClass) {
		Bag<Class<? extends Component>> components = new Bag<Class<? extends Component>>();
		int count = input.readInt();
		for (int i = 0; i < count; i++) {
			int index = input.readShort();
			Class<? extends Component> type = identifiers.idToType.get(index);
			components.add(type);
		}
		return new ArchetypeMapper.TransmuterEntry(components);
	}
}
