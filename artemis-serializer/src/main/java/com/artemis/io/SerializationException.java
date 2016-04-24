package com.artemis.io;

public class SerializationException extends RuntimeException {
	public SerializationException() {
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

	public SerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
