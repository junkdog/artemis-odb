package com.artemis.generator.model.type;

import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class ParTypeWorkaround extends ParameterizedTypeImpl {
    public ParTypeWorkaround(Type rawType, Type... arguments) {
        super(rawType, arguments);
    }
}
