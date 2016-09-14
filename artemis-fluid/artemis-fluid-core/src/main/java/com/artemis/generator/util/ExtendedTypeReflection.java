package com.artemis.generator.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.reflections.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.*;

/**
 * @author Daan van Yperen
 */
public abstract class ExtendedTypeReflection {

    private static Map<Type, Set<Field>> allPublicFields = new HashMap<Type, Set<Field>>();
    private static Map<Type, Set<Method>> allPublicMethods = new HashMap<Type, Set<Method>>();
    private static Map<Type, List<Annotation>> allAnnotations = new HashMap<Type, List<Annotation>>();

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
     * <p>
     * Excludes static.
     */
    @SuppressWarnings("unchecked")
    public static Set<Field> getAllPublicFields(Class type) {
        Set<Field> result = allPublicFields.get(type);
        if (result == null) {
            result = getAllFields(type, withModifier(Modifier.PUBLIC), withoutModifier(Modifier.STATIC));
            allPublicFields.put(type, result);
        }
        return result;
    }

    /**
     * Get all public annotations of type, throughout the hierarchy!
     * Ordered from superclass to subclass.
     */
    @SuppressWarnings("unchecked")
    public static List<Annotation> getAllAnnotations(Class type) {
        List<Annotation> result = allAnnotations.get(type);
        if (result == null) {
            result = getAllAnnotationsList(type);
            allAnnotations.put(type, result);
        }
        return result;
    }

    /** Returns all annotations on hierarchy. Ignores Object and interfaces. */
    public static List<Annotation> getAllAnnotationsList(Class type) {
        ArrayList<Annotation> result = new ArrayList<Annotation>(4);
        for (Class t : getHierarchy(type)) {
            result.addAll(ReflectionUtils.getAnnotations(t));
        }
        return result;
    }

    /** Return class hierarchy, except object. */
    private static List<Class> getHierarchy(Class type) {
        ArrayList<Class> results = new ArrayList<Class>();
        while (type != Object.class && !type.isInterface()) {
            results.add(type);
            type = type.getSuperclass();
        }
        return Lists.reverse(results);
    }


    /**
     * Get all public methods of type, cached.
     * <p>
     * Excludes static, abstract.
     */
    @SuppressWarnings("unchecked")
    public static Set<Method> getAllPublicMethods(Class type) {
        Set<Method> result = allPublicMethods.get(type);
        if (result == null) {
            result = getAllMethods(type, withModifier(Modifier.PUBLIC), withoutModifier(Modifier.ABSTRACT), withoutModifier(Modifier.STATIC), withoutModifier(Modifier.VOLATILE));
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
