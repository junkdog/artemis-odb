package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.Component;

/**
 * Transforms a {@link Component} into a {@link com.artemis.PackedComponent}. Component transformation
 * takes place during the <code>artemis</code> goal defined in <code>artemis-odb-maven-plugin</code>
 * or the <code>weave</code> task in <code>artemis-odb-gradle-plugin</code>.
 *
 * @see <a href="https://github.com/junkdog/artemis-odb/wiki/Packed-Weaver">PackedWeaver</a>
 *	  on the wiki.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface PackedWeaver {
	
	// Note: these will be activate when weaving with sun.misc.Unsafe becomes an option.
	
//	WorldStrategy usage() default WorldStrategy.GLOBAL_CONFIGURATION;
//	DataStrategy strategy() default DataStrategy.GLOBAL_CONFIGURATION;
//	
//	public static enum WorldStrategy {
//		/**
//		 * World strategy is inferred from global configuration.
//		 * It is only ever necessary to override this value when certain components
//		 * can benefit from different weaver strategies.
//		 * <p>
//		 * Defaults to {@link WorldStrategy#SINGLE_WORLD} when no value is given.
//		 * </p>
//		 * TODO: implement + proper example
//		 */
//		GLOBAL_CONFIGURATION,
//		
//		/**
//		 * Component is present in multiple worlds simultaneously. Slower than {@link WorldStrategy#SINGLE_WORLD}.
//		 */
//		MULTI_WORLD,
//		
//		/**
//		 * Only one world instance with this component is present at any one time.
//		 */
//		SINGLE_WORLD;
//	}
//	
//	public static enum DataStrategy {
//		GLOBAL_CONFIGURATION,
//		BYTEBUFFER,
//		SUN_MISC_UNSAFE;
//	}
}
