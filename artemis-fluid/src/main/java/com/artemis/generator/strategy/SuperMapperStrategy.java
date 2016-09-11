package com.artemis.generator.strategy;

import com.artemis.E;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

/**
 * Create static method to obtain instances of E.
 * <p>
 *
 * @author Daan van Yperen
 */
public class SuperMapperStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.name = "SuperMapper";
        model.packageName = "com.artemis";
    }

}
