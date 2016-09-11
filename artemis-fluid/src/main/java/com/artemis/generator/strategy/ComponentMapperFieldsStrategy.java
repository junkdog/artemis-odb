package com.artemis.generator.strategy;

import com.artemis.ComponentMapper;
import com.artemis.E;
import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.FieldBuilder;
import com.artemis.generator.util.MethodBuilder;

/**
 * Add method: create method for component.
 *
 * @author Daan van Yperen
 */
public class ComponentMapperFieldsStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        model.add(createComponentMapper(component));
    }

    private FieldDescriptor createComponentMapper(ComponentDescriptor component) {
        return new FieldBuilder(new ParameterizedTypeImpl(ComponentMapper.class, component.getComponentType()), "m" + component.getName()).build();
    }

}
