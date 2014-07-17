package com.artemis;

import com.artemis.utils.reflect.ClassReflection;


@SuppressWarnings("serial")
public class MundaneWireException extends RuntimeException {

	public MundaneWireException(Class<? extends EntityObserver> klazz) {
		super("Not added to world: " + ClassReflection.getSimpleName(klazz));
	}

	public MundaneWireException(String message, Throwable cause) {
		super(message, cause);
	}

	public MundaneWireException(String message) {
		super(message);
	}
}
