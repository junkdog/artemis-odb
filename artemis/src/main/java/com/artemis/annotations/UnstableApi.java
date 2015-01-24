package com.artemis.annotations;

import java.lang.annotation.*;

/**
 * Classes marked with this annotation may undergo extensive refactoring between
 * releases.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface UnstableApi {
}
