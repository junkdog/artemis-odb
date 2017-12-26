package com.artemis.generator.model;

import com.artemis.Component;
import com.artemis.generator.model.type.TypeDescriptor;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * @author Daan van Yperen
 */
public class FluidTypes {
    public static final Type SUPERMAPPER_TYPE = new TypeDescriptor("com.artemis.SuperMapper");
    public static final Type E_TYPE = new TypeDescriptor("com.artemis.E");
    public static final Type EBAG_TYPE = new TypeDescriptor("com.artemis.EBag");
    public static final Type EXTENDS_COMPONENT_TYPE = new WildcardType() {
        @Override
        public Type[] getUpperBounds() {
            return new Type[]{Component.class};
        }

        @Override
        public Type[] getLowerBounds() {
            return new Type[0];
        }
    };
}
