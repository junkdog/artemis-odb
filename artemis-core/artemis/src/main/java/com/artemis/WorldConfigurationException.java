package com.artemis;

import java.lang.RuntimeException;import java.lang.String;import java.lang.Throwable; /**
 * World configuration failed.
 *
 * @author Daan van Yperen
 */
public class WorldConfigurationException extends RuntimeException {
	public WorldConfigurationException(String msg) {
		super(msg);
	}

	public WorldConfigurationException(String msg, Throwable e) {
		super(msg,e);
	}
}
