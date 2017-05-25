package com.artemis.annotations;

import java.lang.annotation.*;


/**
 * Holds the path or identifier for <code>Prefab</code> types.
 * The value from this annotation is passed to the
 * corresponding <code>PrefabReader</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PrefabData {
	String value();
}
