package com.artemis.annotations;

import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extends the lifecycle of this component type, ensuring removed instances are retrievable until
 * all {@link SubscriptionListener#removed(IntBag) listeners} have been notified - regardless
 * of removal method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DelayedComponentRemoval {}
