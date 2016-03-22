package com.artemis.io;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoIntBagEntitySerializer extends Serializer<IntBag> {
	private final World world;
	private final Bag<Entity> translatedIds = new Bag<Entity>();

	private int recursionLevel;

	public KryoIntBagEntitySerializer (World world) {
		this.world = world;
		world.inject(this);
	}

	@Override
	public void write (Kryo kryo, Output output, IntBag entities) {
		recursionLevel++;

		output.writeInt(entities.size());
		if (recursionLevel == 1) {
			for (int i = 0, s = entities.size(); s > i; i++) {
				Entity e = world.getEntity(entities.get(i));
				output.writeInt(e.getId());
				kryo.writeObject(output, e);
			}
		} else {
			for (int i = 0, s = entities.size(); s > i; i++) {
				output.writeInt(entities.get(i));
			}
		}

		recursionLevel--;
	}

	@Override
	public IntBag read (Kryo kryo, Input input, Class<IntBag> aClass) {
		recursionLevel++;

		IntBag bag = new IntBag();
		int count = input.readInt();
		if (recursionLevel == 1) {
			for (int i = 0; i < count; i++) {
				int oldId = input.readInt();
				Entity e = kryo.readObject(input, Entity.class);
				translatedIds.set(oldId, e);
				bag.add(e.getId());
			}
		} else {
			for (int i = 0; i < count; i++) {
				bag.add(input.readInt());
			}
		}

		recursionLevel--;
		return bag;
	}

	public Bag<Entity> getTranslatedIds() {
		return translatedIds;
	}
}
