package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.systems.EntityProcessingSystem;

/**
 * When optimizing an {@link EntityProcessingSystem}, don't reduce the visibility
 * of {@link EntityProcessingSystem#process()}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface PreserveProcessVisiblity {}
