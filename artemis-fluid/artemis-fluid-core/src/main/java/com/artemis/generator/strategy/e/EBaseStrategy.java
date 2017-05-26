package com.artemis.generator.strategy.e;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.*;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;

/**
 * Generate basic scaffold of E class.
 * <p>
 * - basic class setup (fields, initialization).
 * - static method to obtain instances of E.
 *
 * @author Daan van Yperen
 */
public class EBaseStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.name = "E";
        model.packageName = "com.artemis";
        model.superclass = Entity.class;

        model.add(createStaticMapperField());
        model.add(createInitMethod());
        model.add(createEntityIdGetter());
    }

    private MethodDescriptor createInitMethod() {
        return
                new MethodBuilder(null, "<init>")
                        .parameter(FluidTypes.FLUIDWORLD_TYPE, "world")
                        .parameter(int.class, "entityId")
                        .statement("super(world, entityId)")
                        .build();
    }

    private FieldDescriptor createStaticMapperField() {
        return new FieldBuilder(FluidTypes.FLUIDWORLD_TYPE, "_processingMapper")
                .debugNotes("Default _processingMapper field.")
                .setStatic(true).build();
    }

    /**
     * Getter Entity E::id()
     */
    private MethodDescriptor createEntityIdGetter() {
        return
                new MethodBuilder(int.class, "id")
                        .statement("return id")
                        .build();
    }

}
