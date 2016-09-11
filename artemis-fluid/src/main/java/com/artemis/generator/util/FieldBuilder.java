package com.artemis.generator.util;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;

import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class FieldBuilder {

    private final FieldDescriptor field;

    public FieldBuilder(Type type, String name) {
        field = new FieldDescriptor(type, name);
    }

    public FieldDescriptor build()
    {
        return field;
    }
}
