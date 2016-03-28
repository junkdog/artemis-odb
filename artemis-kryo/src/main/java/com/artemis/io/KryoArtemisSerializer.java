package com.artemis.io;

import com.artemis.*;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.StreamUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ReferenceResolver;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.*;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.kryo.util.Util;
import org.objenesis.strategy.InstantiatorStrategy;

import java.io.*;

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

		// note we don't want to use references for Entity, we use our own stuff so we can keep track of ids
		MapReferenceResolver resolver = new MapReferenceResolver() {
			@Override public boolean useReferences (Class type) {
				if (type == Entity.class) return false;
				return super.useReferences(type);
			}
		};
		kryo = new Kryo(resolver);
		kryo.setRegistrationRequired(false);

		kryo.register(SaveFileFormat.ComponentIdentifiers.class, lookup);
		/* TODO this sorta expects Bag<Entity, what do we do if it doesn't?
		 * we could require custom serializer for stuff with it, but thats kinda crap */
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
			ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
			saveBinary(os, save);

			// do we want this encode/decode stuff? source is short, probably not
			// could add it to util or whatever
			char[] encode = Base64Coder.encode(os.toByteArray());
			String encoded = new String(encode);
//			System.out.println(new String(os.toByteArray()));
			writer.append(encoded);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void saveBinary(OutputStream os, SaveFileFormat save) {
		referenceTracker.inspectTypes(world);
		referenceTracker.preWrite(save);

		save.archetypes = new ArchetypeMapper(world, save.entities);

		componentCollector.preWrite(save);
		entitySerializer.serializationState = save;
		transmuterEntrySerializer.identifiers = save.componentIdentifiers;
		entitySerializer.archetypeMapper = new ArchetypeMapper(world, save.entities);
		entitySerializer.archetypeMapper.serializationState = save;
		save.componentIdentifiers.build();

		Output output = new Output(os);
		kryo.writeObject(output, save.componentIdentifiers);
		kryo.writeObject(output, save.archetypes);
		output.writeInt(save.entities.size());
		kryo.writeObject(output, save);
		output.flush();
		output.close();
		entitySerializer.clearSerializerCache();
	}

	@Override
	protected <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
		try {
			byte[] bytes = StreamUtils.copyStreamToByteArray(is);
			String raw = new String(bytes);
			bytes = Base64Coder.decode(raw);
			is = new ByteBufferInput(bytes);
			return loadBinary(is, format);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends SaveFileFormat> T loadBinary(InputStream is, Class<T> format) {
		entitySerializer.preLoad();

		Input input = new ByteBufferInput(is);

		SaveFileFormat partial = new SaveFileFormat((IntBag)null);
		partial.componentIdentifiers = kryo.readObject(input, SaveFileFormat.ComponentIdentifiers.class);
		transmuterEntrySerializer.identifiers = partial.componentIdentifiers;

		partial.archetypes = kryo.readObject(input, ArchetypeMapper.class);
		entitySerializer.archetypeMapper = partial.archetypes;

		entitySerializer.serializationState = partial;
		if (entitySerializer.archetypeMapper != null) {
			entitySerializer.archetypeMapper.serializationState = partial;
			transmuterEntrySerializer.identifiers = partial.componentIdentifiers;
		}

		referenceTracker.inspectTypes(partial.componentIdentifiers.nameToType.values());
		entitySerializer.factory.configureWith(input.readInt());

		T t = kryo.readObject(input, format);
		t.tracker = entitySerializer.keyTracker;
		referenceTracker.translate(intBagEntitySerializer.getTranslatedIds());
		// TODO do we want to clear those?
		entitySerializer.clearSerializerCache();
		return t;
	}

	public Kryo getKryo () {
		return kryo;
	}
}
