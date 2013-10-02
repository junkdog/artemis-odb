package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Mapper.
 * <p>
 * Used to identify a {@link com.artemis.ComponentMapper} for injection.
 * </p>
 *
 * @author Arni Arent
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mapper {

}
