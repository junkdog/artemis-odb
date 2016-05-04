package com.artemis.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.artemis.EntityFactory;

/**
 * Invokes setter on component, instead of invoking fields. 
 * 
 * @see EntityFactory
 * @see Bind
 * @see Sticky
 */
@Retention(SOURCE)
@Target(METHOD)
@Documented
@Deprecated
public @interface UseSetter {
	String value() default "";
}
