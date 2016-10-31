package com.artemis.generator.common;

import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;

/**
 * Implement for strategies that iterates over model.
 *
 * @author Daan van Yperen
 */
public abstract class IterativeModelStrategy implements BuilderModelStrategy {

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        for (ComponentDescriptor component : artemisModel.components) {
            apply(component, model);
        }
    }

    /** Implementations should transform model based on component. */
    protected abstract void apply(ComponentDescriptor component, TypeModel model);
}
