package com.artemis.io;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.IdentityHashMap;

public class JsonArtemisSerializer extends WorldSerializationManager.ArtemisSerializer<Json.Serializer> {
	private final Json json;
	private final ComponentLookupSerializer lookup;
	private final IntBagEntitySerializer intBagEntitySerializer;
	private final EntitySerializer entitySerializer;

	private boolean prettyPrint;
	private ReferenceTracker referenceTracker;

	public JsonArtemisSerializer(World world) {
		super(world);

		referenceTracker = new ReferenceTracker();

		json = new Json(JsonWriter.OutputType.json);
		json.setIgnoreUnknownFields(true);

		lookup = new ComponentLookupSerializer(world);
		intBagEntitySerializer = new IntBagEntitySerializer(world);

		json.setSerializer(IdentityHashMap.class, lookup);
		json.setSerializer(Bag.class, new EntityBagSerializer(world));
		json.setSerializer(IntBag.class, intBagEntitySerializer);
		entitySerializer = new EntitySerializer(world, referenceTracker);
		json.setSerializer(Entity.class, entitySerializer);
	}

	public JsonArtemisSerializer prettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		return this;
	}

	@Override
	public WorldSerializationManager.ArtemisSerializer register(Class<?> type, Json.Serializer serializer) {
		json.setSerializer(type, serializer);
		return this;
	}

	@Override
	protected void save(Writer writer, SaveFileFormat save) {
		try {
			referenceTracker.inspectTypes(world);
			save.componentIdentifiers.putAll(lookup.classToIdentifierMap());
			if (prettyPrint) {
				writer.append(json.prettyPrint(save));
			} else {
				json.toJson(save, writer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
		updateLookupMap(is); // this isn't the nicest solution, but what the hell
		T t = json.fromJson(format, is);
		referenceTracker.translate(intBagEntitySerializer.getTranslatedIds());
		return t;
	}

	private void updateLookupMap(InputStream is) {
		try {
			JsonValue jsonData = new JsonReader().parse(is);
			SaveFileFormat save = new SaveFileFormat((IntBag)null);
			json.readField(save, "componentIdentifiers", jsonData);

			is.reset();
			entitySerializer.types = save.readLookupMap();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
