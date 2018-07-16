package com.artemis.generator.util;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.AccessLevel;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;

import java.lang.reflect.Type;

/**
 * Helper builder for FieldDescriptor.
 *
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

    public FieldBuilder setStatic(boolean value) {
        field.setStatic(value);
        return this;
    }

    public FieldBuilder setFinal(boolean value) {
        field.setFinal(value);
        return this;
    }

    public FieldBuilder setAccessLevel(AccessLevel value) {
        field.setAccessLevel(value);
        return this;
    }

    public FieldBuilder debugNotes(String s) {
        field.setDebugNotes(s);
        return this;
    }

    public FieldBuilder initializer(String value) {
        field.setInitializer(value);
        return this;
    }
}
