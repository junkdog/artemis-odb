package com.artemis.generator.util;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.AccessLevel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;

/**
 * @author Daan van Yperen
 */
public class MethodBuilder {

    private final MethodDescriptor method;

    public MethodBuilder(Class returnType, String methodName) {
        method = new MethodDescriptor(returnType, methodName);
    }

    public MethodDescriptor build()
    {
        return method;
    }

    public MethodBuilder returnFluid() {
        method.addStatement("return this");
        return this;
    }

    public MethodBuilder mapper(ComponentDescriptor component, String suffix) {
        mapper("", component, suffix);
        return this;
    }

    public MethodBuilder mapper(String prefix, ComponentDescriptor component, String suffix) {
        method.addStatement(prefix + "mappers.m"+component.getName() + suffix);
        return this;
    }

    public MethodBuilder parameter(Class type, String name) {
        method.addParameter(new ParameterDescriptor(type, name));
        return this;
    }

    public MethodBuilder setStatic(boolean value) {
        method.setStatic(value);
        return this;
    }

    /** Add body, excluding outer brackets. */
    public MethodBuilder body(String value) {
        String[] split = value.split("\n");
        for (String statement : split) {
            method.addStatement(statement);
        }
        return this;
    }

    public MethodBuilder statement(String statement) {
        method.addStatement(statement);
        return this;
    }

    public MethodBuilder accessLevel(AccessLevel level) {
        method.setAccessLevel(level);
        return this;
    }
}
