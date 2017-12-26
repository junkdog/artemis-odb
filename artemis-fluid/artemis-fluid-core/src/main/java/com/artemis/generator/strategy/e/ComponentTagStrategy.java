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

    /**
     * static EBag E::withGroup(groupName)
     */
    private MethodDescriptor createStaticWithTag() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "withTag")
                        .setStatic(true)
                        .parameter(String.class, "tag")
                        .javaDoc("Get entity by tag.\n@return {@code E}, or {@code null} if no such tag.")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("int id=_processingMapper.getWorld().getSystem(com.artemis.managers.TagManager.class).getEntityId(tag)")
                        .statement("return id != -1 ? E(id) : null")
                        .build();
    }


    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(createTagMethodSetter());
        model.add(createTagMethodGetter());
        model.add(createStaticWithTag());
    }
}
