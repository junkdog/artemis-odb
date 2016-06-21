package com.artemis.io;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Transient;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;

import java.util.*;

/**
 * <p>
 * Represents a set of Entities ready to be serialized, or a set of Entities that was just
 * deserialized (and therefore ready to use in your game). This class can be extended if additional
 * data requires persisting. All instance fields in this class - or its children - are persisted.
 * </p>
 *
 * <p>The default de/serializer backend provided is
 * {@code JsonArtemisSerializer}. A kryo backend is planned for a later release.
 * A backend knows how to serialize entities and metadata, but little else.
 * If extending this class, custom per-type serializers can be defined - these
 * serializers are convenient to have, but normal POJO classes with some
 * custom logic works too.</p>
 *
 * <p>The typical custom serializer works on type, e.g. a <code>GameStateManager</code>
 * contains additional data not available to components directly. A serializer would
 * be registered to only interact with that class; during loading and saving, the
 * serializer interacts directly with the manager and reads/writes the data as needed.</p>
 *
 * @see EntityReference
 */
public class SaveFileFormat {

	// all non-transient fields are automatically serialized
	public Metadata metadata;
	public ComponentIdentifiers componentIdentifiers;
	public IntBag entities;
	public ArchetypeMapper archetypes;

	transient SerializationKeyTracker tracker = new SerializationKeyTracker();

	public SaveFileFormat(IntBag entities) {
		this.entities = (entities != null) ? entities : new IntBag();
		componentIdentifiers = new ComponentIdentifiers();
		metadata = new Metadata();
		metadata.version = Metadata.LATEST;
	}


	public SaveFileFormat(EntitySubscription es) {
		this(es.getEntities());
	}

	public SaveFileFormat() {
		this((IntBag)null);
	}

	public final Entity get(String key) {
		return tracker.get(key);
	}

	public final boolean has(String key) {
		return tracker.get(key) != null;
	}

	public final Set<String> keys() {
		return tracker.keys();
	}

	public static class Metadata {
		public static final int VERSION_1 = 1;
		public static final int LATEST = VERSION_1;

		public int version;
	}

	public static class ComponentIdentifiers {
		public Map<Class<? extends Component>, String> typeToName =
			new IdentityHashMap<Class<? extends Component>, String>();
		private Map<String, Class<? extends Component>> nameToType =
			new HashMap<String, Class<? extends Component>>();
		public Map<Class<? extends Component>, Integer> typeToId =
			new HashMap<Class<? extends Component>, Integer>();
		public Map<Integer, Class<? extends Component>> idToType =
			new HashMap<Integer, Class<? extends Component>>();

		transient Set<Class<? extends Component>> transientComponents =
			new HashSet<Class<? extends Component>>();

		void build() {
			if (typeToName.size() > 0)
				buildFromNames();
			else
				buildFromIndices();
		}

		public Class<? extends Component> getType(String name) {
			Class<? extends Component> type = nameToType.get(name);
			if (type == null)
				throw new SerializationException("No component type with name: " + name);

			return type;
		}

		public Collection<Class<? extends Component>> getTypes() {
			return nameToType.values();
		}

		private void buildFromNames() {
			Iterator<Map.Entry<Class<? extends Component>, String>> it = typeToName.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Class<? extends Component>, String> entry = it.next();
				Class<? extends Component> c = entry.getKey();
				if (ClassReflection.getDeclaredAnnotation(c, Transient.class) == null) {
					nameToType.put(entry.getValue(), c);
					if (typeToId.get(c) == null) {
						typeToId.put(c, nameToType.size());
						idToType.put(nameToType.size(), c);
					}
				} else {
					transientComponents.add(c);
					it.remove();
				}
			}
		}

		private void buildFromIndices() {
			Iterator<Map.Entry<Integer, Class<? extends Component>>> it = idToType.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Class<? extends Component>> entry = it.next();
				Class<? extends Component> c = entry.getValue();
				if (ClassReflection.getDeclaredAnnotation(c, Transient.class) == null) {
					typeToId.put(c, nameToType.size());
				} else {
					transientComponents.add(c);
					it.remove();
				}
			}
		}

		boolean isTransient(Class<? extends Component> c) {
			return transientComponents.contains(c);
		}
	}
}
