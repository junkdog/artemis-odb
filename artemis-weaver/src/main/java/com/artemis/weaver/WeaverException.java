package com.artemis.weaver;

@SuppressWarnings("serial")
public class WeaverException extends RuntimeException {

	public WeaverException() {
		super();
	}

	public WeaverException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeaverException(String message) {
		super(message);
	}

	public WeaverException(Throwable cause) {
		super(cause);
	}
}
