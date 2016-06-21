package com.artemis.io;

import com.artemis.*;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.*;
import com.esotericsoftware.kryo.util.MapReferenceResolver;

import java.io.*;

/**
 * {@link com.artemis.managers.WorldSerializationManager.ArtemisSerializer} implementation with {@link Kryo} as a backend.
 *
 * {@link Kryo#setRegistrationRequired(boolean)} is set to {@code true} by default for security purposes. It can be changed by accessing Kryo instance via {@link #getKryo()}
 *
 * All custom {@link Component} serializers must extend {@link com.artemis.io.KryoEntitySerializer.ComponentFieldSerializer}
 * All {@link IntBag}s are treated as annotated with {@link com.artemis.annotations.EntityId}, if component has IntBag for other purpose, custom serializer is required.
 * Only {@link Bag}s of {@link Entity}s are supported, if component has other type of Bag, custom serializer is required.
 *
 */
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
		kryo.register(Bag.class, new KryoEntityBagSerializer(world));
		kryo.register(IntBag.class, intBagEntitySerializer);
		kryo.register(Entity.class, entitySerializer);
		kryo.register(ArchetypeMapper.class, new KryoArchetypeMapperSerializer());
		kryo.register(ArchetypeMapper.TransmuterEntry.class, transmuterEntrySerializer);

		kryo.register(SaveFileFormat.class);
		kryo.register(SaveFileFormat.Metadata.class);
	}

	@Override
	public WorldSerializationManager.ArtemisSerializer register (Class<?> type, Serializer serializer) {
		kryo.register(type, serializer);
		return this;
	}

	public WorldSerializationManager.ArtemisSerializer register (Class<?> type) {
		kryo.register(type);
		return this;
	}

	@Override
	public void save(OutputStream os, SaveFileFormat save)
			throws SerializationException {

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
	public  <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
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

		referenceTracker.inspectTypes(partial.componentIdentifiers.typeToId.keySet());
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
