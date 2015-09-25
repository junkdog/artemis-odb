package com.artemis.weaver.optimizer;

import com.artemis.meta.ClassMetadata;

public enum EntitySystemType {
	ENTITY_PROCESSING(
		"com/artemis/systems/EntityProcessingSystem",
		"com/artemis/EntitySystem"),
	ITERATING(
		"com/artemis/systems/IteratingSystem",
		"com/artemis/BaseEntitySystem");

	public final String superName;
	public final String replacedSuperName;

	EntitySystemType(String superName, String replacedSuperName) {
		this.superName = superName;
		this.replacedSuperName = replacedSuperName;
	}

	public static EntitySystemType resolve(ClassMetadata meta) {
		return resolve(meta.superClass);
	}

	public static EntitySystemType resolve(String owner) {
		for (EntitySystemType type : EntitySystemType.values()) {
			if (owner.equals(type.superName))
				return type;
		}

		return null;
	}
}
