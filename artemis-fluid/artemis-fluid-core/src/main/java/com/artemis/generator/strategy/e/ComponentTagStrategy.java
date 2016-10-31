package com.artemis.generator.strategy.e;

import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

/**
 * Generates tag accessor for E.
 *
 * @author Daan van Yperen
 */
public class ComponentTagStrategy implements BuilderModelStrategy {

    private MethodDescriptor createTagMethodSetter() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "tag")
                        .parameter(String.class, "tag")
                        .debugNotes("default tag setter")
                        .statement("mappers.getWorld().getSystem(com.artemis.managers.TagManager.class).register(tag, entityId)")
                        .returnFluid()
                        .build();
    }

    private MethodDescriptor createTagMethodGetter() {
        return
                new MethodBuilder(String.class, "tag")
                        .debugNotes("default tag getter")
                        .statement("return mappers.getWorld().getSystem(com.artemis.managers.TagManager.class).getTag(entityId)")
                        .build();
    }

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(createTagMethodSetter());
        model.add(createTagMethodGetter());
    }
}
