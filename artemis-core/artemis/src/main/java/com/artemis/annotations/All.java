package com.artemis.annotations;

import com.artemis.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Auto-configures fields or systems pertaining to aspects.
 *
 * <p>On fields, this annotation works similar to {@link Wire}; fields are configured
 * during {@link EntitySystem#initialize()}, or explicitly via {@link World#inject(Object)}.</p>
 *
 * <p>On BaseEntitySystem subclasses, this annotation configures the aspects for the system,
 *  replacing the need to use constructor parameters.</>/p>
 *
 * The annotated field must be one the following types: {@link Archetype}, {@link Aspect}, {@link Aspect.Builder},
 * {@link EntitySubscription}, {@link EntityTransmuter}.</p>
 * 
 * <p>This annotation can be combined with {@link One} and {@link Exclude}, 
 * but will be ignored if {@link AspectDescriptor} is present.</p>
 *
 * <h4>Note on EntityTransmuters/Archetypes</h4>
 * <p>{@link #value()} corresponds to create.</p>
 * 
 * @see One
 * @see Exclude
 * @see AspectDescriptor
 * @see Wire
 * 
 * @author Ken Schosinsky
 * @author Felix Bridault
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Documented
@UnstableApi
public @interface All {

	/**
	 * @return required types
	 */
	Class<? extends Component>[] value() default {};
}
