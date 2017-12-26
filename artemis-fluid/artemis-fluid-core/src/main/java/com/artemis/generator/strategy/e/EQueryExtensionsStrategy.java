package com.artemis.generator.strategy.e;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

import java.lang.reflect.Type;

/**
 * Strategy with additional ways to find entities.
 *
 * @author Daan van Yperen
 */
public class EQueryExtensionsStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(createStaticWithAspect());
        model.add(createStaticWithComponent());
    }

    /**
     * static EBag E::withAspect(aspect)
     */
    private MethodDescriptor createStaticWithAspect() {
        return
                new MethodBuilder(FluidTypes.EBAG_TYPE, "withAspect")
                        .setStatic(true)
                        .parameter(Aspect.Builder.class, "aspect")
                        .javaDoc("Get all entities matching aspect.\nFor performance reasons do not create the aspect every call.\n@return {@code EBag} of entities matching aspect. Returns empty bag if no entities match aspect.")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new EBag(_processingMapper.getWorld().getAspectSubscriptionManager().get(aspect).getEntities())")
                        .build();
    }

    /**
     * static EBag E::withComponent(component)
     */
    private MethodDescriptor createStaticWithComponent() {
        return
                new MethodBuilder(FluidTypes.EBAG_TYPE, "withComponent")
                        .setStatic(true)
                        .parameter(new ParameterizedTypeImpl(Class.class, FluidTypes.EXTENDS_COMPONENT_TYPE), "component")
                        .javaDoc("Get all entities with component.\nThis is a relatively costly operation. For performance use withAspect instead.\n@return {@code EBag} of entities matching aspect. Returns empty bag if no entities match aspect.")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new EBag(_processingMapper.getWorld().getAspectSubscriptionManager().get(Aspect.all(component)).getEntities())")
                        .build();
    }
}
