package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.components.SerializationTag;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Wire(failOnNull = false)
public class KryoEntitySerializer extends Serializer<Entity> {

	private final Bag<Component> components = new Bag<Component>();
	private final ComponentNameComparator comparator = new ComponentNameComparator();
	private final World world;
	private final ReferenceTracker referenceTracker;
	private final DefaultObjectStore defaultValues;
	final EntityPoolFactory factory;

	private GroupManager groupManager;
	private TagManager tagManager;
	private final Collection<String> registeredTags;

	private boolean isSerializingEntity;

	private ComponentMapper<SerializationTag> saveTagMapper;

	SerializationKeyTracker keyTracker;
	ArchetypeMapper archetypeMapper;
	SaveFileFormat serializationState;

	public KryoEntitySerializer (World world, ReferenceTracker referenceTracker) {
		this.world = world;
		this.referenceTracker = referenceTracker;
		defaultValues = new DefaultObjectStore();
		factory = new EntityPoolFactory(world);
		world.inject(this);

		registeredTags = (tagManager != null)
			? tagManager.getRegisteredTags()
			: Collections.<String>emptyList();
	}

	void setUsePrototypes(boolean usePrototypes) {
		defaultValues.setUsePrototypes(usePrototypes);
	}

	void preLoad() {
		keyTracker = new SerializationKeyTracker();
	}

	@Override
	public void write (Kryo kryo, Output output, Entity e) {
		// need to track this in case the components of an entity
		// reference another entity - if so, we only want to record
		// the id
		if (isSerializingEntity) {
			output.writeInt(e.getId());
			return;
		} else {
			isSerializingEntity = true;
		}

		world.getComponentManager().getComponentsFor(e.getId(), components);
		components.sort(comparator);

		// write archetype id
		output.writeInt(e.getCompositionId());

		// write tag
		boolean hasTag = false;
		for (String tag : registeredTags) {
			if (tagManager.getEntity(tag) != e)
				continue;
			output.writeString(tag);
			hasTag = true;
			break;
		}
		if (!hasTag) {
			output.writeString(null);
		}

		// write key tag
		if (saveTagMapper.has(e)) {
			String key = saveTagMapper.get(e).tag;
			output.writeString(key);
		} else {
			output.writeString(null);
		}

		// write group
		if (groupManager == null) {
			output.writeInt(0);
		} else {
			ImmutableBag<String> groups = groupManager.getGroups(e);
			if (groups.size() == 0) {
				output.writeInt(0);
			} else {
				output.writeInt(groups.size());
				for (String group : groups) {
					output.writeString(group);
				}
			}
		}

		// write components
		SaveFileFormat.ComponentIdentifiers identifiers = serializationState.componentIdentifiers;

		int count = getComponentCount(identifiers);
		output.writeInt(count);

		for (int i = 0, s = components.size(); s > i; i++) {
			Component c = components.get(i);
			if (identifiers.isTransient(c.getClass()))
				continue;

			if (defaultValues.hasDefaultValues(c))
				continue;

			output.writeShort(identifiers.typeToId.get(c.getClass()));
			kryo.writeObject(output, c, serializer(kryo, c.getClass(), null));
		}
		components.clear();

		isSerializingEntity = false;
	}

	private int getComponentCount(SaveFileFormat.ComponentIdentifiers identifiers) {
		int count = 0;
		for (int i = 0, s = components.size(); s > i; i++) {
			Component c = components.get(i);
			if (identifiers.isTransient(c.getClass()))
				continue;

			if (defaultValues.hasDefaultValues(c))
				continue;

			count++;
		}
		return count;
	}

	@Override
	public Entity read (Kryo kryo, Input input, Class<Entity> aClass) {
		// need to track this in case the components of an entity
		// reference another entity - if so, we only want to read
		// the id
		if (isSerializingEntity) {
			int entityId = input.readInt();
			// creating a temporary entity; this will later be translated
			// to the correct entity
			return FakeEntityFactory.create(world, entityId);
		} else {
			isSerializingEntity = true;
		}

		Entity e = factory.createEntity();

		// read archetype
		int archetype = input.readInt();
		// read tag
		String tag = input.readString();
		if (tag != null) {
			tagManager.register(tag, e);
		}
		// read key tag
		String keyTag = input.readString();
		if (keyTag != null) {
			keyTracker.register(keyTag, e);
			saveTagMapper.create(e).tag = keyTag;
		}
		// read groups
		int groupCount = input.readInt();
		for (int i = 0; i < groupCount; i++) {
			groupManager.add(e, input.readString());
		}
		// read components
		SaveFileFormat.ComponentIdentifiers identifiers = serializationState.componentIdentifiers;

		int count = input.readInt();
		archetypeMapper.transmute(e, archetype);

		final EntityEdit edit = e.edit();
		for (int i = 0; i < count; i++) {
			int id = input.readShort();
			final Class<? extends Component> type = identifiers.idToType.get(id);
			// note we use custom serializer because we must use edit.create(T) for non basic types
			Component c = kryo.readObject(input, type, serializer(kryo, type, edit));
			referenceTracker.addEntityReferencingComponent(c);
		}

		isSerializingEntity = false;

		return e;
	}

	private Map<Class, Serializer> serializers = new HashMap<Class, Serializer>();

	private Serializer serializer (Kryo kryo, Class<? extends Component> type, EntityEdit edit) {
		Serializer serializer = serializers.get(type);
		if (serializer == null) {
			serializer = kryo.getSerializer(type);
			if (serializer.getClass() == FieldSerializer.class) {
				serializer = new ComponentFieldSerializer(kryo, type);
			}
			serializers.put(type, serializer);
		}
		if (serializer instanceof ComponentFieldSerializer) {
			((ComponentFieldSerializer)serializer).init(edit);
		} else {
			throw new RuntimeException("Custom serializer for " + type + " must extend ComponentFieldSerializer.");
		}
		return serializer;
	}

	protected void clearSerializerCache() {
		serializers.clear();
	}

	public static class ComponentFieldSerializer<T extends Component> extends FieldSerializer<T> {
		protected EntityEdit edit;

		public ComponentFieldSerializer (Kryo kryo, Class<? extends Component> type) {
			super(kryo, type);
		}

		public ComponentFieldSerializer init (EntityEdit edit) {
			this.edit = edit;
			return this;
		}

		@Override protected T create (Kryo kryo, Input input, Class type) {
			return (T)edit.create(type);
		}
	}
}
