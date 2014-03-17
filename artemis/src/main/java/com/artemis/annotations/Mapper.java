package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.ComponentMapper;
import com.artemis.World;


/**
 * Reflexively injects {@link ComponentMapper} fields upon calling {@link World#setSystem(com.artemis.EntitySystem)}
 * or {@link World#setManager(com.artemis.Manager)}.
 * <p>
 *
 * @deprecated See {@link Wire}.
 *
 * @author Arni Arent
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface Mapper {}
