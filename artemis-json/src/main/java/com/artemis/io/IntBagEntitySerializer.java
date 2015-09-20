package com.artemis.io;

import com.artemis.EntityHelper;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

public class IntBagEntitySerializer implements JsonSerializer<IntBag> {
	private final World world;
	private final IntBag translatedIds = new IntBag();

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
				int e = world.getEntity(entities.get(i));
				json.writeValue(Integer.toString(e), new TemporaryEntity(e));
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
				TemporaryEntity e = json.readValue(TemporaryEntity.class, entity.child);
				translatedIds.set(Integer.parseInt(entity.name), e.id);
				bag.add(e.id);

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

	public IntBag getTranslatedIds() {
		return translatedIds;
	}
}
