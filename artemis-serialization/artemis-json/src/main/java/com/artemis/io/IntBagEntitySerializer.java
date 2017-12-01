package com.artemis.io;

import com.artemis.Entity;
import com.artemis.SerializationEntityProvider;
import com.artemis.World;
import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

public class IntBagEntitySerializer implements JsonSerializer<IntBag> {
	@SkipWire private final World world;
	private final Bag<Entity> translatedIds = new Bag<Entity>();

	private int recursionLevel;

	public IntBagEntitySerializer(World world) {
		this.world = world;
		world.inject(this);
	}

	@Override
	public void write(Json json, IntBag entities, Class knownType) {
		recursionLevel++;

		if (recursionLevel == 1) {
			json.writeObjectStart();
			for (int i = 0, s = entities.size(); s > i; i++) {
				Entity e = ((SerializationEntityProvider)world).getEntity(entities.get(i));
				json.writeValue(Integer.toString(e.getId()), e);
			}
			json.writeObjectEnd();
		} else {
			json.writeArrayStart();
			for (int i = 0, s = entities.size(); s > i; i++) {
				json.writeValue(entities.get(i));
			}
			json.writeArrayEnd();
		}

		recursionLevel--;
	}

	@Override
	public IntBag read(Json json, JsonValue jsonData, Class type) {
		recursionLevel++;

		IntBag bag = new IntBag();
		if (recursionLevel == 1) {
			JsonValue entityArray = jsonData.child;
			JsonValue entity = entityArray;
			while (entity != null) {
				Entity e = json.readValue(Entity.class, entity.child);
				translatedIds.set(Integer.parseInt(entity.name), e);
				bag.add(e.getId());

				entity = entity.next;
			}
		} else {
			for (JsonValue child = jsonData.child; child != null; child = child.next) {
				bag.add(json.readValue(Integer.class, child));
			}
		}

		recursionLevel--;

		return bag;

	}

	public Bag<Entity> getTranslatedIds() {
		return translatedIds;
	}
}
