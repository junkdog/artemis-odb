package com.artemis.io;

import com.artemis.Entity;

import java.util.*;

/**
 * Maintains serialization-local key-to-entity mappings.
 */
class SerializationKeyTracker {
	private Map<String, Entity> keyToEntity = new HashMap<String, Entity>();

	void register(String key, Entity e) {
		keyToEntity.put(key, e);
	}

	Entity get(String key) {
		return keyToEntity.get(key);
	}

	Set<String> keys() {
		return keyToEntity.keySet();
	}
}
