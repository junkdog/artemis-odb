package com.artemis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking component types for easy retrieval by the the validation processor.
 * Automatically inherited by all components.
 */
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@interface ComponentConformanceValidator {}