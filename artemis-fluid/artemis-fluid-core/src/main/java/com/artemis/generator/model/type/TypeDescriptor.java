package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * Fake type.
 *
 * Bit of a hack, we need this to refer to types that have not been generated yet, like SuperMapper and E.
 *
 * @todo is there an idiomatic solution?
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
