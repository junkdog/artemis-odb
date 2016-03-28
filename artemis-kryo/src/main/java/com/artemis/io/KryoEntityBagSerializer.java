package com.artemis.io;

import com.artemis.World;
import com.artemis.utils.Bag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoEntityBagSerializer extends Serializer<Bag> {
	private final World world;

	public KryoEntityBagSerializer (World world) {
		this.world = world;
		world.inject(this);
	}

	@Override
	public void write (Kryo kryo, Output output, Bag bag) {
		output.writeInt(bag.size());
		for (Object item : bag) {
			kryo.writeClassAndObject(output, item);
		}
	}

	@Override
	public Bag read (Kryo kryo, Input input, Class<Bag> aClass) {
		Bag result = new Bag();
		int count = input.readInt();
		for (int i = 0; i < count; i++) {
			result.add(kryo.readClassAndObject(input));
		}
		return result;
	}
}
