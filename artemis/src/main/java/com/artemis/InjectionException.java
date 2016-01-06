package com.artemis;

/**
 * Injection failed.
 *
 * @author Daan van Yperen
 */
public class InjectionException extends RuntimeException {
	public InjectionException(String msg) {
		super(msg);
	}

	public InjectionException(String msg, Throwable e) {
		super(msg,e);
	}
}
