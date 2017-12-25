package com.artemis.annotations;

import com.artemis.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the required components on an entity for the annotated system.
 *
 * @author Daan van Yperen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@UnstableApi
public @interface Exclude {
    Class<? extends Component>[] value();
}
