package com.artemis.io;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

@Wire
public class IntBagEntitySerializer implements Json.Serializer<IntBag> {
	private final World world;

	public IntBagEntitySerializer(World world) {

		this.world = world;
		world.inject(this);
	}

	@Override
	public void write(Json json, IntBag entities, Class knownType) {
		json.writeObjectStart();
		for (int i = 0, s = entities.size(); s > i; i++) {
			Entity e = world.getEntity(entities.get(i));
			json.writeValue(Integer.toString(e.id), e);
		}
		json.writeObjectEnd();
	}

	@Override
	public IntBag read(Json json, JsonValue jsonData, Class type) {
		IntBag bag = new IntBag();
		JsonValue entityArray = jsonData.child;
		JsonValue entity = entityArray;
		while (entity != null) {
			Entity e = json.readValue(Entity.class, entity.child);
			entity = entity.next;
			bag.add(e.id);
		}

		return bag;
	}
}
