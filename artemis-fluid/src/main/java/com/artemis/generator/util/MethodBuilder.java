package com.artemis.generator.util;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;

/**
 * Created by Daan on 10-9-2016.
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
        method.addStatement("return this;");
        return this;
    }

    public MethodBuilder mapper(ComponentDescriptor component, String suffix) {
        mapper("", component, suffix);
        return this;
    }

    public MethodBuilder mapper(String prefix, ComponentDescriptor component, String suffix) {
        method.addStatement(prefix + "getMapper("+ component.getClass()+")" + suffix);
        return this;
    }

    public MethodBuilder parameter(Class type, String name) {
        method.addParameter(new ParameterDescriptor(type, name));
        return this;
    }
}
