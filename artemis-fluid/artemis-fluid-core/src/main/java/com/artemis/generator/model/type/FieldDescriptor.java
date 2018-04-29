package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * Describes field.
 *
 * @author Daan van Yperen
 */
public class FieldDescriptor implements AmbiguousSignature {
    public Type type;
    public String name;
    private boolean isStatic;
    private boolean isFinal;
    private AccessLevel accessLevel = AccessLevel.PROTECTED;
    private String debugNotes;
    public String initializer;

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

    public void setFinal(boolean value) {
        this.isFinal = value;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public String ambiguousSignature() {
        return name;
    }

    public void setDebugNotes(String debugNotes) {
        this.debugNotes = debugNotes;
    }

    public String getDebugNotes() {
        return debugNotes;
    }

    public String getInitializer() {
        return initializer;
    }

    public void setInitializer(String initializer) {
        this.initializer = initializer;
    }
}
