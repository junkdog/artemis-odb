package com.artemis;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ArtemisMultiException extends RuntimeException {
	private final List<Throwable> exceptions = new ArrayList<Throwable>();
	
	public ArtemisMultiException(List<Throwable> exceptions) {
		super();
		exceptions.addAll(exceptions);
	}

	public List<Throwable> getExceptions() {
		return exceptions;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Throwable t : exceptions) {
			if (sb.length() > 0) sb.append("\n");
			sb.append(t.getMessage());
		}
		return sb.toString();
	}
}
