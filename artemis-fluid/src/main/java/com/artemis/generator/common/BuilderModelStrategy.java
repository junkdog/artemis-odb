package com.artemis.generator.common;

import com.artemis.Component;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;

import java.util.Collection;

/**
 * Strategy for generating builder model from component set.
 *
 * Created by Daan on 10-9-2016.
 */
public interface BuilderModelStrategy {

    /** Apply strategy to model, generating whatever methods needed. */
    void apply(Collection<ComponentDescriptor> components, ClassModel model);
}
