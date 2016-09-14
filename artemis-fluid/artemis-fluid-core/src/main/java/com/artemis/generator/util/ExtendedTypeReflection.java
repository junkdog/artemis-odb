package com.artemis.generator.util;

import com.google.common.base.Predicate;
import org.reflections.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withModifier;

/**
 * @author Daan van Yperen
 */
public abstract class ExtendedTypeReflection {

    private static Map<Type,Set<Field>> allPublicFields = new HashMap<Type,Set<Field>>();
    private static Map<Type,Set<Method>> allPublicMethods = new HashMap<Type,Set<Method>>();
    private static Map<Type,Set<Annotation>> allAnnotations = new HashMap<Type,Set<Annotation>>();

    /**
     * Get if component is a flag component.
     *
     * @return {@code true} is simple flag, {@code false} if it is a data container.
     */
    public static boolean isFlagComponent(Class type) {
        return getAllPublicFields(type).isEmpty() &&
                getAllPublicMethods(type).isEmpty();
    }

    /**
     * Get all public fields of this type, cached.
     */
    @SuppressWarnings("unchecked")
    public static Set<Field> getAllPublicFields(Class type) {
        Set<Field> result = allPublicFields.get(type);
        if (result == null) {
            result = getAllFields(type, withModifier(Modifier.PUBLIC));
            allPublicFields.put(type, result);
        }
        return result;
    }

    /**
     * Get all public fields of type, cached.
     */
    @SuppressWarnings("unchecked")
    public static Set<Annotation> getAllAnnotations(Class type) {
        Set<Annotation> result = allAnnotations.get(type);
        if (result == null) {
            result = ReflectionUtils.getAllAnnotations(type);
            allAnnotations.put(type, result);
        }
        return result;
    }

    /**
     * Get all public methods of type, cached.
     */
    @SuppressWarnings("unchecked")
    public static Set<Method> getAllPublicMethods(Class type) {
        Set<Method> result = allPublicMethods.get(type);
        if (result == null) {
            result = getAllMethods(type, withModifier(Modifier.PUBLIC), withoutModifier(Modifier.ABSTRACT));
            allPublicMethods.put(type, result);
        }
        return result;
    }

    public static <T extends Member> Predicate<T> withoutModifier(final int mod) {
        return new Predicate<T>() {
            public boolean apply(@Nullable T input) {
                return input != null && (input.getModifiers() & mod) == 0;
            }
        };
    }

}
