package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * Fake type.
 *
 * @author Daan van Yperen
 */
public class TypeDescriptor implements Type {

    private String name;

    public TypeDescriptor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
