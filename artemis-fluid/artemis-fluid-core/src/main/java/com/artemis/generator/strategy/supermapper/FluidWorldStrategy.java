package com.artemis.generator.strategy.supermapper;

import com.artemis.*;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.*;
import com.artemis.generator.util.MethodBuilder;

/**
 * Generate basic scaffold for SuperMapper class.
 *
 * @author Daan van Yperen
 */
public class FluidWorldStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.name = "FluidWorld";
        model.packageName = "com.artemis";
        model.superclass =
                new ParTypeWorkaround(FluidTypes.COSPLAYWORLD_TYPE,FluidTypes.E_TYPE);
        model.add(createConstructorMethod());
        model.add(createProcessingMethod());
    }

    private MethodDescriptor createConstructorMethod() {
        return new MethodBuilder(null, "<init>")
                .accessLevel(AccessLevel.PUBLIC)
                .parameter(WorldConfiguration.class, "configuration")
                .statement("super(configuration, new EntityFactory<com.artemis.FluidWorld, com.artemis.E>() { @Override public com.artemis.E instance(FluidWorld world, int entityId) { return new com.artemis.E(world, entityId); } }, com.artemis.E.class)")
                .build();
    }

    private MethodDescriptor createProcessingMethod() {
        return new MethodBuilder(void.class, "processSystem")
                .accessLevel(AccessLevel.PUBLIC)
                .build();
    }
}
