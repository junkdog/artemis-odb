package com.artemis.generator.model.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private Type rawType;
    private Type[] arguments;

    public ParameterizedTypeImpl(Type rawType, Type... arguments) {
        this.rawType = rawType;
        this.arguments = arguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return arguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
