package com.artemis.generator.strategy.e;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.generator.util.Strings;

/**
 * Adds methods to check if entity has a component.
 *
 * @author Daan van Yperen
 */
public class ComponentExistStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        model.add(createHasComponentMethod(component));
    }

    /**
     * boolean E::hasComponent()
     */
    private MethodDescriptor createHasComponentMethod(ComponentDescriptor component) {
        return
                new MethodBuilder(boolean.class,

                        Strings.assembleMethodName(component.getPreferences().getPrefixComponentHas(),component.getName()))
                        .debugNotes(component.getComponentType().getName())
                        .mapper("return ", component, ".has(entityId)")
                        .build();
    }
}
