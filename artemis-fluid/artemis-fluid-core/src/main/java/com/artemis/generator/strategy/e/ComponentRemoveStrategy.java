package com.artemis.generator.strategy.e;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.generator.util.Strings;

/**
 * Generates remove method for each component type.
 *
 * @author Daan van Yperen
 */
public class ComponentRemoveStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        model.add(removeComponentStrategy(component));
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor removeComponentStrategy(ComponentDescriptor component) {
        return
                new MethodBuilder(FluidTypes.E_TYPE,
                        Strings.assembleMethodName(component.getPreferences().getPrefixComponentRemove(),component.getName()))
                        .debugNotes(component.getComponentType().getName())
                        .mapper(component, ".remove(entityId)")
                        .returnFluid()
                        .build();
    }
}
