package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class FieldDescriptor {
    public Type type;
    public String name;

    public FieldDescriptor(Type type, String name) {
        this.type = type;
        this.name = name;
    }
}
