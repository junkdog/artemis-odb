package com.artemis.generator.common;

import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Write fluid class model.
 *
 * Created by Daan on 10-9-2016.
 */

public class BuilderModelGenerator {

    private List<BuilderModelStrategy> strategies = new LinkedList<BuilderModelStrategy>();

    /** Add strategy used to convert components to agnostic builder model. */
    public void addStrategy( BuilderModelStrategy strategy )
    {
        strategies.add(strategy);
    }

    /** Generate a builder based on component model. */
    public ClassModel generate(Collection<ComponentDescriptor> components) {
        Preconditions.checkArgument(!strategies.isEmpty(),"No strategies registered to generate model.");

        ClassModel result = new ClassModel();

        for (BuilderModelStrategy strategy : strategies) {
            strategy.apply(components, result);
        }

        return result;
    }
}