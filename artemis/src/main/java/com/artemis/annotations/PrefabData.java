package com.artemis.annotations;

import java.lang.annotation.*;


/**
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PrefabData {
	String value();
}
