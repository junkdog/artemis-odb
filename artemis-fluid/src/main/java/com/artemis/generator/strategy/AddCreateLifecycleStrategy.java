package com.artemis.generator.strategy;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;
import com.artemis.generator.model.MethodDescriptor;
import com.artemis.generator.util.MethodBuilder;

/**
 * Adds basic lifecycle methods to agnostic model.
 *
 * Created by Daan on 10-9-2016.
 */
public class AddCreateLifecycleStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, ClassModel model) {
        model.add(createCreateMethod(component));
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor createCreateMethod(ComponentDescriptor component) {
        return
                new MethodBuilder("E", component.getMethodPrefix())
                        .mapper(component, ".create();")
                        .returnFluid()
                        .build();
    }
}
