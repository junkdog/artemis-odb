package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.ComponentMapper;
import com.artemis.EntityFactory;
import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.World;


/**
 * Reflexively injects {@link ComponentMapper}, {@link EntitySystem},
 * {@link Manager} and {@link EntityFactory} fields upon calling
 * {@link World#setSystem(com.artemis.BaseSystem)} or
 * {@link World#setManager(com.artemis.Manager)}.
 * 
 * <p>
 *
 * Inject into any object using <code>@Wire</code> and {@link World#inject(Object)}
 *
 * Nonstandard dependency fields must be explicitly annotated with
 * <code>@Wire(name="myName")</code> to inject by name, or <code>@Wire</code>
 * to inject by type. Class level <code>@Wire</code> annotation is not enough.
 *
 * To specify which nonstandard dependencies to inject, use
 * {@link com.artemis.WorldConfiguration#register(String, Object)} and
 * {@link com.artemis.WorldConfiguration#register(Object)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface Wire {
	
	/**
	 * If true, also inject inherited fields.
	 */
	boolean injectInherited() default false;
	
	
	/**
	 * Throws a {@link NullPointerException} if field can't be injected.
	 */
	boolean failOnNull() default true;
	
	
	/**
	 * 
	 */
	String name() default "";
}
