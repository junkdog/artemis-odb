package com.artemis.io;

import com.artemis.Component;
import com.artemis.ComponentCollector;
import com.artemis.EntityHelper;
import com.artemis.World;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;

public class JsonArtemisSerializer extends WorldSerializationManager.ArtemisSerializer<Json.Serializer> {
	private final Json json;
	private final ComponentLookupSerializer lookup;
	private final IntBagEntitySerializer intBagEntitySerializer;
	private final EntitySerializer entitySerializer;
	private final ComponentCollector componentCollector;

	private boolean prettyPrint;
	private ReferenceTracker referenceTracker;

	public JsonArtemisSerializer(World world) {
		super(world);

		componentCollector = new ComponentCollector(world);
		referenceTracker = new ReferenceTracker(world);

		lookup = new ComponentLookupSerializer(world);
		intBagEntitySerializer = new IntBagEntitySerializer(world);
		entitySerializer = new EntitySerializer(world, referenceTracker);

		json = new Json(JsonWriter.OutputType.json);
		json.setIgnoreUnknownFields(true);
		json.setSerializer(IdentityHashMap.class, lookup);
		json.setSerializer(IntBag.class, intBagEntitySerializer);
		json.setSerializer(int.class, entitySerializer);
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
			referenceTracker.preWrite(save);

			componentCollector.preWrite(save);
			entitySerializer.preWrite(save);
			lookup.setComponentMap(save.componentIdentifiers);
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
		referenceTracker.inspectTypes(updateLookupMap(is).values());
		T t = json.fromJson(format, is);
		referenceTracker.translate(intBagEntitySerializer.getTranslatedIds());
		return t;
	}

	private void inspectComponentTypes(SaveFileFormat save) {
		BitSet compositionIds = new BitSet();
		int[] ids = save.entities.getData();
		for (int i = 0, s = save.entities.size(); s > i; i++) {
			int e = world.getEntity(ids[i]);
			compositionIds.set(EntityHelper.getCompositionId(world,e));
		}

	}

	private Map<String, Class<? extends Component>> updateLookupMap(InputStream is) {
		try {
			JsonValue jsonData = new JsonReader().parse(is);
			SaveFileFormat save = new SaveFileFormat((IntBag)null);
			json.readField(save, "componentIdentifiers", jsonData);

			InputStreamHelper.reset(is);
			Map<String, Class<? extends Component>> lookup = save.readLookupMap();
			entitySerializer.types = lookup;

			return lookup;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
