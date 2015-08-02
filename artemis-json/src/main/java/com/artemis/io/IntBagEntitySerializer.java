package com.artemis.io;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

@Wire
public class IntBagEntitySerializer implements JsonSerializer<IntBag> {
	private final World world;
	private final Bag<Entity> translatedIds = new Bag<Entity>();

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
			translatedIds.set(Integer.parseInt(entity.name), e);
			bag.add(e.id);

			entity = entity.next;
		}

		return bag;
	}

	public Bag<Entity> getTranslatedIds() {
		return translatedIds;
	}
}
