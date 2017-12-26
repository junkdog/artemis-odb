package com.artemis.generator.model;

import com.artemis.generator.model.type.TypeDescriptor;

import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class FluidTypes {
    public static final Type SUPERMAPPER_TYPE = new TypeDescriptor("com.artemis.SuperMapper");
    public static final Type E_TYPE = new TypeDescriptor("com.artemis.E");
    public static final Type EBAG_TYPE = new TypeDescriptor("com.artemis.EBag");
}
