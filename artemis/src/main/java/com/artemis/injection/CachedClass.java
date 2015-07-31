package com.artemis.injection;

import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.Field;

/**
 * @author Snorre E. Brekke
 */
class CachedClass {
    public CachedClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> clazz;
    public Field[] allFields;
    public Wire wireAnnotation;
    public boolean injectInherited;
    public boolean failOnNull;
    public WireType wireType;
}
