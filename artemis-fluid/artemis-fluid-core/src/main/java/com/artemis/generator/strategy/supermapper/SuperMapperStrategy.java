package com.artemis.generator.strategy.supermapper;

import com.artemis.BaseSystem;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.AccessLevel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

/**
 * Generate basic scaffold for SuperMapper class.
 *
 * @author Daan van Yperen
 */
public class SuperMapperStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.name = "SuperMapper";
        model.packageName = "com.artemis";
        model.superclass = BaseSystem.class;
        model.add(createInitializationMethod());
    }

    private MethodDescriptor createInitializationMethod() {
        return new MethodBuilder(void.class, "processSystem")
                .accessLevel(AccessLevel.PUBLIC)
                .statement("E._processingMapper=this")
                .build();
    }

}
