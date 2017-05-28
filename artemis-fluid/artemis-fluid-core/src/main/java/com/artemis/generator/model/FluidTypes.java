package com.artemis.generator.model;

import com.artemis.generator.model.type.TypeDescriptor;

import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class FluidTypes {
    public static final Type FLUIDWORLD_TYPE = new TypeDescriptor("com.artemis.FluidWorld");
    public static final Type E_TYPE = new TypeDescriptor("com.artemis.E");
    public static final Type COSPLAYWORLD_TYPE = new TypeDescriptor("com.artemis.AbstractEntityWorld");
}
