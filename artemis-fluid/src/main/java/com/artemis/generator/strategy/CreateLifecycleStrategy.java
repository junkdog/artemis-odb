package com.artemis.generator.strategy;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;
import com.artemis.generator.model.MethodDescriptor;
import com.artemis.generator.util.MethodBuilder;

/**
 * Add method: create method for component.
 *
 * Created by Daan on 10-9-2016.
 */
public class CreateLifecycleStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, ClassModel model) {
        model.add(createComponentMethod(component));
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor createComponentMethod(ComponentDescriptor component) {
        return
                new MethodBuilder("E", component.getMethodPrefix())
                        .mapper(component, ".create();")
                        .returnFluid()
                        .build();
    }
}
