package com.artemis.generator.strategy.e;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;

import java.lang.reflect.Field;

/**
 * Strategy for extending the fluid interface based on component fields.
 *
 * Extend this and put it as a dependency on the fluid grade or maven module. The fluid interface generator will
 * scan the classpath for strategies and automatically call them.
 *
 * @see com.artemis.generator.strategy.e.DefaultFieldProxyStrategy for example.
 * @author Daan van Yperen
 */
public interface FieldProxyStrategy {

    /**
     * priority of this strategy compared to others.
     * Higher priority strategies will get first chance to match fields. Use {@code 0} for default.
     * @return desired priority.
     */
    int priority();

    /**
     * @param component Artemis component.
     * @param field Field to be proxied.
     * @param model Type model to extend.
     * @return {@code true} if this strategy wants to handle the proxy.
     */
    boolean matches(ComponentDescriptor component, Field field, TypeModel model);

    /**
     * Apply changes to model. Will be called only once per field.
     *
     * @param component Artemis component.
     * @param field Field to be proxied.
     * @param model Type model to extend.
     */
    void execute(ComponentDescriptor component, Field field, TypeModel model);
}
