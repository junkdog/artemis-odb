package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class CallableTransmuter implements Callable<Void>
{
	private final String file;
	
	protected abstract void process(String file) throws FileNotFoundException, IOException;

	@Override
	public final Void call() throws Exception
	{
		process(file);
		return null;
	}
}
