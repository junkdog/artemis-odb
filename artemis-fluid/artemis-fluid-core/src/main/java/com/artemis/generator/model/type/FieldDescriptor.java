package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * Describes field.
 *
 * @author Daan van Yperen
 */
public class FieldDescriptor {
    public Type type;
    public String name;
    private boolean isStatic;
    private AccessLevel accessLevel = AccessLevel.PROTECTED;

    public FieldDescriptor(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setStatic(boolean value) {
        this.isStatic = value;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
