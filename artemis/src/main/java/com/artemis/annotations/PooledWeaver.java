package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.Component;
import com.artemis.PackedComponent;

/**
 * Transforms a {@link Component} into a {@link PackedComponent}. Component transformation
 * takes place during the <code>artemis</code> goal defined in <code>artemis-odb-maven-plugin</code>.
 * 
 * @see <a href="https://github.com/junkdog/artemis-odb/wiki/Component%20Types">Component types</a>
 *      on the wiki.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PooledWeaver {
	
	/**
	 * If true, forces weaving even if maven property <code>enablePooledWeaving</code> is
	 * set to <code>false</code>. 
	 */
	boolean forceWeaving() default false;
}
