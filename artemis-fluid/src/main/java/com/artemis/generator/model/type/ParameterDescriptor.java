package com.artemis.generator.model.type;

/**
 * Created by Daan on 11-9-2016.
 */
public class ParameterDescriptor {
    private Class type;
    private String name;

    public ParameterDescriptor(Class type, String name) {
        this.type = type;
        this.name = name;
    }
}
