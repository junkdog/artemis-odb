package com.artemis.utils;

import com.artemis.utils.reflect.ClassReflection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class InterfaceUtil {

    /**
     * @param objects systems available.
     * @param clazz   implemented class.
     * @return All systems that implement given class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] getObjectsImplementing(ImmutableBag objects, Class<T> clazz, T[] t) {
        final List<T> list = new ArrayList<>();
        for (Object o : objects) {
            if (o != null && ClassReflection.isInstance(clazz, o)) {
                list.add((T)o);
            }
        }
        return !list.isEmpty() ? list.toArray(t) : t;
    }

}
