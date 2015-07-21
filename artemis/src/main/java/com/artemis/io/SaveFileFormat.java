package com.artemis.io;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * <p>The default save file format. This class can be extended if additional
 * data requires persisting. All instance fields in this class - or its children -
 * are persisted.</p>
 *
 * <p>Two default de/serializer back-ends are provided: {@link KryoArtemisSerializer}
 * and {@link JsonArtemisSerializer}. These know how to serialize entities
 * and metadata, but little else. Custom serializers can be written for both
 * backends, and are required when extending the save file format.</p>
 *
 * <p>The typical custom serializer works on type, e.g. a <code>GameStateManager</code>
 * contains additional data not available to components directly. A serializer would
 * be registered to only interact with that class; during loading and saving, the
 * serializer interacts directly with the manager and reads/writes the data as needed.</p>
 *
 * <p><b>Nota Bene:</b> PackedComponent types are not yet supported.</p>
 *
 * @See {@link JsonArtemisSerializer}
 * @See {@link KryoArtemisSerializer}
 * @See {@link EntityReference}
 */
@Wire
public class SaveFileFormat {

	// all fields are automatically serialized

	public Metadata metadata;
	public final IdentityHashMap<Class<? extends Component>, String> componentIdentifiers;
	public final IntBag entities;

	public SaveFileFormat(IntBag entities) {
		this.entities = entities;
		componentIdentifiers = new IdentityHashMap<Class<? extends Component>, String>();
		metadata = new Metadata();
	}

	public SaveFileFormat(EntitySubscription es) {
		this(es.getEntities());
	}

	private SaveFileFormat() {
		this((IntBag)null);
	}

	public static class Metadata {
		public static final int VERSION_1 = 1;
		public static final int LATEST = VERSION_1;

		public int version;

		public Metadata() {
			version = LATEST;
		}
	}
}
