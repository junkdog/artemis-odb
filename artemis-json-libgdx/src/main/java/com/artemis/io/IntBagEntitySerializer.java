package com.artemis.io;

import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class IntBagEntitySerializer implements Json.Serializer<IntBag> {
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
			JsonValue entity = jsonData.child;
			while (entity != null) {
				int e = json.readValue(TemporaryEntity.class, entity.child).id;
				translatedIds.set(Integer.parseInt(entity.name), e);
				bag.add(e);

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
