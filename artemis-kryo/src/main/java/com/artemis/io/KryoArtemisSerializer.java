package com.artemis.io;

import com.artemis.*;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.StreamUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.InstantiatorStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class KryoArtemisSerializer extends WorldSerializationManager.ArtemisSerializer<Serializer> {
	private final Kryo kryo;
	private final KryoComponentLookupSerializer lookup;
	private final KryoIntBagEntitySerializer intBagEntitySerializer;
	private final KryoTransmuterEntrySerializer transmuterEntrySerializer;
	private final KryoEntitySerializer entitySerializer;
	private final ComponentCollector componentCollector;

	private ReferenceTracker referenceTracker;

	public KryoArtemisSerializer (World world) {
		super(world);

		componentCollector = new ComponentCollector(world);
		referenceTracker = new ReferenceTracker(world);

		lookup = new KryoComponentLookupSerializer();
		intBagEntitySerializer = new KryoIntBagEntitySerializer(world);
		entitySerializer = new KryoEntitySerializer(world, referenceTracker);
		transmuterEntrySerializer = new KryoTransmuterEntrySerializer();

		kryo = new Kryo();
		kryo.setRegistrationRequired(false);

		// note this is here so entity references work, but could break other stuff
		kryo.setReferences(false);

		kryo.register(SaveFileFormat.ComponentIdentifiers.class, lookup);
		kryo.register(Bag.class, new KryoEntityBagSerializer(world));
		kryo.register(IntBag.class, intBagEntitySerializer);
		kryo.register(Entity.class, entitySerializer);
		kryo.register(ArchetypeMapper.class, new KryoArchetypeMapperSerializer());
		kryo.register(ArchetypeMapper.TransmuterEntry.class, transmuterEntrySerializer);
	}

	public void setKryoInstantiatorStrategy (InstantiatorStrategy strategy) {
		kryo.setInstantiatorStrategy(strategy);
	}

	@Override
	public WorldSerializationManager.ArtemisSerializer register (Class<?> type, Serializer serializer) {
		kryo.register(type, serializer);
		return this;
	}

	@Override
	protected void save(Writer writer, SaveFileFormat save) {
		try {
			referenceTracker.inspectTypes(world);
			referenceTracker.preWrite(save);

			save.archetypes = new ArchetypeMapper(world, save.entities);

			componentCollector.preWrite(save);
			entitySerializer.serializationState = save;
			transmuterEntrySerializer.identifiers = save.componentIdentifiers;
			entitySerializer.archetypeMapper = new ArchetypeMapper(world, save.entities);
			entitySerializer.archetypeMapper.serializationState = save;
			save.componentIdentifiers.build();

			Output output = new Output(new ByteBufferOutput());
			kryo.writeObjectOrNull(output, save.componentIdentifiers, SaveFileFormat.ComponentIdentifiers.class);
			kryo.writeObjectOrNull(output, save.archetypes, ArchetypeMapper.class);
			output.writeInt(save.entities.size());
			kryo.writeObjectOrNull(output, save, save.getClass());
			// do we want this encode/decode stuff? source is short
			// could add it to util or whatever
			char[] encode = Base64Coder.encode(output.getBuffer());
			String encoded = new String(encode);
			writer.append(encoded);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
		try {
			entitySerializer.preLoad();

			byte[] bytes = StreamUtils.copyStreamToByteArray(is);
			String raw = new String(bytes);
			bytes = Base64Coder.decode(raw);
			Input input = new ByteBufferInput(bytes);

			SaveFileFormat partial = new SaveFileFormat((IntBag)null);
			partial.componentIdentifiers = kryo.readObjectOrNull(input, SaveFileFormat.ComponentIdentifiers.class);
			transmuterEntrySerializer.identifiers = partial.componentIdentifiers;

			partial.archetypes = kryo.readObjectOrNull(input, ArchetypeMapper.class);
			entitySerializer.archetypeMapper = partial.archetypes;

			entitySerializer.serializationState = partial;
			if (entitySerializer.archetypeMapper != null) {
				entitySerializer.archetypeMapper.serializationState = partial;
				transmuterEntrySerializer.identifiers = partial.componentIdentifiers;
			}

			referenceTracker.inspectTypes(partial.componentIdentifiers.nameToType.values());
			entitySerializer.factory.configureWith(input.readInt());

			T t = kryo.readObjectOrNull(input, format);
			t.tracker = entitySerializer.keyTracker;
			// FIXME this is broken due to kryo loading entities in order then bags for
			// whatever stupid reason, disabling refs fixes that
			referenceTracker.translate(intBagEntitySerializer.getTranslatedIds());
			return t;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
