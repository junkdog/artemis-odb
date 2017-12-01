package com.artemis.generator.strategy.e;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.ExtendedTypeReflection;
import com.artemis.generator.util.MethodBuilder;

import java.lang.reflect.Modifier;

import static org.reflections.ReflectionUtils.*;

/**
 * Generate boolean accessors for flag components.
 * <p>
 * Flag components are all components with no public fields and methods.
 *
 * @author Daan van Yperen
 */
public class FlagComponentBooleanAccessorStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        Class type = component.getComponentType();

        if (component.isFlagComponent()) {
            model.add(createCheckFlagComponentExistenceMethod(component));
            model.add(createFlagComponentToggleMethod(component));
        }
    }

    private MethodDescriptor createFlagComponentToggleMethod(ComponentDescriptor component) {
        return
                new MethodBuilder(FluidTypes.E_TYPE, component.getMethodPrefix())
                        .parameter(boolean.class, "value")
                        .mapper(component, ".set(id, value)")
                        .debugNotes("flag component(=field/method-less) " + component.getComponentType().getName())
                        .returnFluid()
                        .build();
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor createCheckFlagComponentExistenceMethod(ComponentDescriptor component) {
        return
                new MethodBuilder(boolean.class, "is" + component.getName())
                        .debugNotes("flag component(=field/method-less) " + component.getComponentType().getName())
                        .mapper("return ", component, ".has(id)")
                        .build();
    }
}
