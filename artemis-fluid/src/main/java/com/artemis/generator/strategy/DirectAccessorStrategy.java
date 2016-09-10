package com.artemis.generator.strategy;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;
import com.artemis.generator.model.MethodDescriptor;
import com.artemis.generator.util.MethodBuilder;

/**
 * Add method: direct accessor for components.
 *
 * Created by Daan on 10-9-2016.
 */
public class DirectAccessorStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, ClassModel model) {
        model.add(createGetComponentMethod(component));
    }

    /**
     * T _componentName() -> return instance of entity Component E::_componentName()
     */
    private MethodDescriptor createGetComponentMethod(ComponentDescriptor component) {
        return
                new MethodBuilder(component.getName(), "_"+component.getMethodPrefix())
                        .mapper("return ", component, ".get();")
                        .build();
    }
}
