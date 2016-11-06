package com.artemis.prefab;

import com.artemis.io.SerializationException;

public class MissingPrefabDataException extends SerializationException {
	public MissingPrefabDataException() {
	}

	public MissingPrefabDataException(String message) {
		super(message);
	}

	public MissingPrefabDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPrefabDataException(Throwable cause) {
		super(cause);
	}
}
