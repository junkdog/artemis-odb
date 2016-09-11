package com.artemis.generator.model.type;

/**
  * @author Daan van Yperen
 */
public class ParameterDescriptor {
    public Class type;
    public String name;

    public ParameterDescriptor(Class type, String name) {
        this.type = type;
        this.name = name;
    }
}
