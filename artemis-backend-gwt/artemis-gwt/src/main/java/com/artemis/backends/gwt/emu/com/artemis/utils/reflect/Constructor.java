/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.artemis.utils.reflect;

import com.artemis.gwtref.client.Parameter;
import java.lang.reflect.Modifier;

/** Provides information about, and access to, a single constructor for a Class.
 * @author nexsoftware */
public final class Constructor {

	private final com.artemis.gwtref.client.Constructor constructor;

	Constructor (com.artemis.gwtref.client.Constructor constructor) {
		this.constructor = constructor;
	}

	/** Returns an array of Class objects that represent the formal parameter types, in declaration order, of the constructor. */
	public Class[] getParameterTypes () {
		Parameter[] parameters = constructor.getParameters();
		Class[] parameterTypes = new Class[parameters.length];
		for (int i = 0, j = parameters.length; i < j; i++) {
			parameterTypes[i] = parameters[i].getClazz();
		}
		return parameterTypes;
	}

	/** Returns the Class object representing the class or interface that declares the constructor. */
	public Class getDeclaringClass () {
		return constructor.getEnclosingType();
	}

	public boolean isAccessible () {
		return constructor.isPublic();
	}

	public void setAccessible (boolean accessible) {
		// NOOP in GWT
	}

	public int getModifiers() {
		return Modifier.PUBLIC; // sorry
	}

	/** Uses the constructor to create and initialize a new instance of the constructor's declaring class, with the supplied
	 * initialization parameters. */
	public Object newInstance (Object... args) throws ReflectionException {
		try {
			return constructor.newInstance(args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(),
					e);
		}
	}

}