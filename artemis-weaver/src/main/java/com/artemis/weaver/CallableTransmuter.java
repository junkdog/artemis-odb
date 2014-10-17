package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class CallableTransmuter<T> implements Callable<T> {
	private final String file;
	
	protected abstract T process(String file) throws FileNotFoundException, IOException;

	@Override
	public final T call() throws Exception {
		T t = process(file);
		return t;
	}
}
