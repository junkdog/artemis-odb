package com.artemis.generator.common;

import com.artemis.Component;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;

import java.util.Collection;

/**
 * Implement for strategies that iterates over model.
 *
 * Created by Daan on 10-9-2016.
 */
public abstract class IterativeModelStrategy implements BuilderModelStrategy {

    @Override
    public void apply(Collection<ComponentDescriptor> components, ClassModel model) {
        for (ComponentDescriptor component : components) {
            apply(component, model);
        }
    }

    /** Implementations should transform model based on component. */
    protected abstract void apply(ComponentDescriptor component, ClassModel model);
}
