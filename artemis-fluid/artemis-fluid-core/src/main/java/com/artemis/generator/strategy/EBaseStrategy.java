package com.artemis.generator.strategy;

import com.artemis.E;
import com.artemis.SuperMapper;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.utils.Bag;

/**
 * Create static method to obtain instances of E.
 * <p>
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
        return new FieldDescriptor(new ParameterizedTypeImpl(Bag.class, E.class), "instances");
    }

    private MethodDescriptor createInitMethod() {
        return
                new MethodBuilder(E.class, "init")
                        .parameter(SuperMapper.class, "superMapper")
                        .parameter(int.class, "entityId")
                        .statement("this.superMapper = superMapper")
                        .statement("this.entityId = entityId")
                        .returnFluid()
                        .build();
    }

    private FieldDescriptor createEntityIdField() {
        return new FieldBuilder(int.class,"entityId").build();
    }

    private FieldDescriptor createMapperField() {
        return new FieldBuilder(SuperMapper.class,"mappers").build();
    }

    private FieldDescriptor createStaticMapperField() {
        return new FieldBuilder(SuperMapper.class,"_processingMapper").setStatic(true).build();
    }

    /**
     * E::E(int)
     */
    private MethodDescriptor createStaticInstancerMethod() {
        return
                new MethodBuilder(E.class, "E")
                        .setStatic(true)
                        .parameter(int.class, "entityId")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new E().init(_processingMapper,entityId)")
                        .build();
    }

}
