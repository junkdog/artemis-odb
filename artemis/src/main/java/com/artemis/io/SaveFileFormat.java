package com.artemis.io;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * <p>The default save file format. This class can be extended if additional
 * data requires persisting. All instance fields in this class - or its children -
 * are persisted.</p>
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
 * <p><b>Nota Bene:</b> PackedComponent types are not yet supported.</p>
 *
 * @See JsonArtemisSerializer
 * @See N/A KryoArtemisSerializer
 * @See {@link EntityReference}
 */
@Wire
public class SaveFileFormat {

	// all fields are automatically serialized

	public Metadata metadata;
	public final IdentityHashMap<Class<? extends Component>, String> componentIdentifiers;
	public final IntBag entities;

	public SaveFileFormat(IntBag entities) {
		this.entities = (entities != null) ? entities : new IntBag();
		componentIdentifiers = new IdentityHashMap<Class<? extends Component>, String>();
		metadata = new Metadata();
		metadata.version = Metadata.LATEST;
	}


	public SaveFileFormat(EntitySubscription es) {
		this(es.getEntities());
	}

	private SaveFileFormat() {
		this((IntBag)null);
	}

	protected Map<String, Class<? extends Component>> readLookupMap() {
		Map<String, Class<? extends Component>> lookup
				= new HashMap<String, Class<? extends Component>>();

		for (Map.Entry<Class<? extends Component>, String> entry : componentIdentifiers.entrySet()) {
			lookup.put(entry.getValue(), entry.getKey());
		}

		return lookup;
	}

	public static class Metadata {
		public static final int VERSION_1 = 1;
		public static final int LATEST = VERSION_1;

		public int version;
	}
}
