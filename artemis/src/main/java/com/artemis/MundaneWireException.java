package com.artemis;


@SuppressWarnings("serial")
public class MundaneWireException extends RuntimeException {

	public MundaneWireException(Class<? extends EntityObserver> klazz) {
		super(String.format("Not added to world: %s", klazz.getName()));
	}

	public MundaneWireException(String message, Throwable cause) {
		super(message, cause);
	}

	public MundaneWireException(String message) {
		super(message);
	}
}
