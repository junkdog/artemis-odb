package com.artemis.annotations;

import java.lang.annotation.*;

/**
 * Customize how the fluid generator processes a component method.
 *
 * Is ignored when your fluid generator is not used in your project.
 *
 * @author Daan van Yperen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FluidMethod {
    /**
     * Exclude method from fluid interface.
     */
    boolean exclude() default false;
}