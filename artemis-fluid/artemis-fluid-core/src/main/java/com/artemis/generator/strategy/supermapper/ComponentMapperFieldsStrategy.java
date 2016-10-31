package com.artemis.generator.strategy.supermapper;

import com.artemis.ComponentMapper;
import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.*;
import com.artemis.generator.util.FieldBuilder;

/**
 * Generates component mappers for each component.
 *
 * @author Daan van Yperen
 */
public class ComponentMapperFieldsStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        model.add(createComponentMapper(component));
    }

    private FieldDescriptor createComponentMapper(ComponentDescriptor component) {
        return new FieldBuilder(new ParameterizedTypeImpl(ComponentMapper.class, component.getComponentType()), "m" + component.getName())
                .debugNotes(component.getComponentType().getName())
                .setAccessLevel(AccessLevel.PUBLIC)
                .build();
    }

}
