package com.artemis.fieldstrategies;

import com.artemis.ProxyExperiment;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.e.FieldProxyStrategy;

import java.lang.reflect.Field;

/**
 * Used for testing priority.
 *
 * @see ExperimentFieldProxyStrategy
 * @author Daan van Yperen
 */
public class ExperimentLowPriorityFieldProxyStrategy implements FieldProxyStrategy {

    @Override
    public int priority() {
        return -1;
    }

    @Override
    public boolean matches(ComponentDescriptor component, Field field, TypeModel model) {
        return ProxyExperiment.class.equals(field.getGenericType());
    }

    @Override
    public void execute(ComponentDescriptor component, Field field, TypeModel model) {
        // should never be called.
    }
}
