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
public class DeleteFromWorldStrategy implements BuilderModelStrategy {

    private MethodDescriptor deleteFromWorldMethod() {
        return
                new MethodBuilder(void.class, "deleteFromWorld")
                        .debugNotes("default delete from world")
                        .statement("mappers.getWorld().delete(entityId)")
                        .build();
    }

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(deleteFromWorldMethod());
    }
}
