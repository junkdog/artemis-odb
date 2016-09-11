package com.artemis.generator.strategy;

import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.*;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.utils.Bag;

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
        model.add(createStaticInstancerMethod());
    }

    private FieldDescriptor createInstancePoolField() {
        return new FieldDescriptor(new ParameterizedTypeImpl(Bag.class, FluidTypes.E_TYPE), "instances");
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
        return new FieldBuilder(int.class,"entityId").build();
    }

    private FieldDescriptor createMapperField() {
        return new FieldBuilder(FluidTypes.SUPERMAPPER_TYPE,"mappers").build();
    }

    private FieldDescriptor createStaticMapperField() {
        return new FieldBuilder(FluidTypes.SUPERMAPPER_TYPE,"_processingMapper").setStatic(true).build();
    }

    /**
     * E::E(int)
     */
    private MethodDescriptor createStaticInstancerMethod() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "E")
                        .setStatic(true)
                        .parameter(int.class, "entityId")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new E().init(_processingMapper,entityId)")
                        .build();
    }

}
