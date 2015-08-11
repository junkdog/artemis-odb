package com.artemis.io;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

@Wire
public class EntityBagSerializer implements Json.Serializer<Bag> {
	private final World world;

	public EntityBagSerializer(World world) {
		this.world = world;
		world.inject(this);
	}

	@Override
	public void write(Json json, Bag bag, Class knownType) {
		json.writeArrayStart();
		for (Object item : bag)
			json.writeValue(item);
		json.writeArrayEnd();
	}

	@Override
	public Bag read(Json json, JsonValue jsonData, Class type) {
		Bag<Entity> result = new Bag<Entity>();
		for (JsonValue child = jsonData.child; child != null; child = child.next)
			result.add(json.readValue(Entity.class, child));

		return result;
	}
}
