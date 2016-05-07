package com.artemis.annotations;

import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guarantees that the decorated component is still present during
 * {@link SubscriptionListener#removed(IntBag)} - regardless of
 * removal method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface  DelayedComponentDeletion {}
