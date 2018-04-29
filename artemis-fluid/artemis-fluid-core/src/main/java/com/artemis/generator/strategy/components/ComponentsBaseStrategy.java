package com.artemis.generator.strategy.components;

import com.artemis.BaseSystem;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.AccessLevel;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.utils.Bag;

/**
 * Generate basic scaffold for SuperMapper class.
 *
 * @author Daan van Yperen
 */
public class ComponentsBaseStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.name = "C";
        model.packageName = "com.artemis";
    }
}
