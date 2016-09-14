package com.artemis.generator.strategy.e;

import com.artemis.Entity;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.*;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;

/**
 * Generate basic scaffold of E class.
 *
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
        model.add(createMapperField());
        model.add(createStaticMapperField());
        model.add(createEntityIdField());
        model.add(createInitMethod());
        model.add(createStaticInstancerMethodByInt());
        model.add(createStaticInstancerMethodByEntity());
    }

    private MethodDescriptor createInitMethod() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "init")
                        .parameter(FluidTypes.SUPERMAPPER_TYPE, "mappers")
                        .parameter(int.class, "entityId")
                        .statement("this.mappers = mappers")
                        .statement("this.entityId = entityId")
                        .returnFluid()
                        .build();
    }

    private FieldDescriptor createEntityIdField() {
        return new FieldBuilder(int.class,"entityId")
                .debugNotes("Default entityId field.")
                .build();
    }

    private FieldDescriptor createMapperField() {
        return new FieldBuilder(FluidTypes.SUPERMAPPER_TYPE,"mappers")
                .debugNotes("Default mappers field.")
                .build();
    }

    private FieldDescriptor createStaticMapperField() {
        return new FieldBuilder(FluidTypes.SUPERMAPPER_TYPE,"_processingMapper")
                .debugNotes("Default _processingMapper field.")
                .setStatic(true).build();
    }

    /**
     * E::E(int)
     */
    private MethodDescriptor createStaticInstancerMethodByInt() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "E")
                        .setStatic(true)
                        .parameter(int.class, "entityId")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new E().init(_processingMapper,entityId)")
                        .build();
    }

    /**
     * E::E(int)
     */
    private MethodDescriptor createStaticInstancerMethodByEntity() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "E")
                        .setStatic(true)
                        .parameter(Entity.class, "entity")
                        .statement("return E(entity.getId())")
                        .build();
    }

}
