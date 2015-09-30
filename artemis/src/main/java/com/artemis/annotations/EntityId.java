package com.artemis.annotations;

import com.artemis.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Using this annotation, entity references can be safely serialized.
 *
 * This feature is only for serialization, and does not protect your
 * references from dangling when entities go out of scope.
 *
 * see https://github.com/junkdog/artemis-odb/wiki/Entity-References-and-Serialization
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityId {
}
