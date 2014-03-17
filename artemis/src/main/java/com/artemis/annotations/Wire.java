package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.ComponentMapper;
import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.World;


/**
 * Reflexively injects {@link ComponentMapper}, {@link EntitySystem} and {@link Manager} fiekds upon
 * calling {@link World#setSystem(com.artemis.EntitySystem)} or {@link World#setManager(com.artemis.Manager)}.
 * <p>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Wire {}
