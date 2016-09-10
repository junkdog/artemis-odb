package com.artemis.generator.strategy;

import com.artemis.E;
import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.util.MethodBuilder;

/**
 * Add method: create method for component.
 *
 * Created by Daan on 10-9-2016.
 */
public class CreateLifecycleStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        model.add(createComponentMethod(component));
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor createComponentMethod(ComponentDescriptor component) {
        return
                new MethodBuilder(E.class, component.getMethodPrefix())
                        .mapper(component, ".create();")
                        .returnFluid()
                        .build();
    }
}
