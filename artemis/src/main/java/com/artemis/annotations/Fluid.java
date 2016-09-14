package com.artemis.annotations;

import java.lang.annotation.*;

/**
 * @author Daan van Yperen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Fluid {
    String name() default "";
}