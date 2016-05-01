package com.artemis.weaver;

import java.io.IOException;
import java.util.concurrent.Callable;

abstract class CallableTransmuter<T> implements Callable<T> {
	private final String file;

	protected CallableTransmuter(String file) {
		this.file = file;
	}

	protected abstract T process(String file) throws IOException;

	@Override
	public final T call() throws Exception {
		T t = process(file);
		return t;
	}
}
