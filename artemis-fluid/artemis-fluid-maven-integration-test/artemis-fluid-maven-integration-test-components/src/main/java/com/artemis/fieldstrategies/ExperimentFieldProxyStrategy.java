package com.artemis.fieldstrategies;

import com.artemis.ProxyExperiment;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.e.FieldProxyStrategy;
import com.artemis.generator.util.MethodBuilder;

import java.lang.reflect.Field;

/**
 * @see ExperimentLowPriorityFieldProxyStrategy
 * @author Daan van Yperen
 */
public class ExperimentFieldProxyStrategy implements FieldProxyStrategy {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean matches(ComponentDescriptor component, Field field, TypeModel model) {
        return ProxyExperiment.class.equals(field.getGenericType());
    }

    @Override
    public void execute(ComponentDescriptor component, Field field, TypeModel model) {
        model.add(fieldGetterMethod(component, field));
    }

    /**
     * Experiment E::proxyStrategyWorked()
     */
    private MethodDescriptor fieldGetterMethod(ComponentDescriptor component, Field field) {
        return new MethodBuilder(field.getGenericType(), "proxyStrategyWorked")
                .mapper("return ", component, ".create(entityId)." + field.getName())
                .build();
    }
}
