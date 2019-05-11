package com.artemis.generator.strategy.e;

import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

/**
 * Adds deleteFromWorld() to fluid interface.
 *
 * @author Daan van Yperen
 */
public class EToStringStrategy implements BuilderModelStrategy {

    private MethodDescriptor toStringMethod() {
        return
                new MethodBuilder(String.class, "toString")
                        .debugNotes("default toString")
                        .statement("return \"E{id=\" + entityId + \"}\"")
                        .build();
    }

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(toStringMethod());
    }
}
