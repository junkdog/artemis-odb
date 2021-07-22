package com.artemis.generator.util;

import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.reflections.ReflectionUtils;

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
            public boolean apply(T input) {
                return input != null && (input.getModifiers() & mod) == 0;
            }
        };
    }

    /**
     * Resolve the actual type of the provided field.
     * For non-generic fields, this will just return the field type.
     * For generic fields, try to determine the provided generic type arguments and deduce the real type from it.
     */
    public static Type resolveGenericType(final ComponentDescriptor component, final Field field) {
        return resolveGenericType(component.getComponentType(), field.getDeclaringClass(), field.getGenericType());
    }

    /**
     * Resolve the actual type of the methods parameter.
     * For non-generic methods, this will just return the parameters type.
     * For generic methods, try to determine the provided generic type arguments and deduce the real type from it.
     */
    public static Type resolveGenericType(final ComponentDescriptor component, final Method method, final Type type) {
        return resolveGenericType(component.getComponentType(), method.getDeclaringClass(), type);
    }

    /**
     * Resolve the actual return type of the method.
     * For non-generic methods, this will just return the parameters type.
     * For generic methods, try to determine the provided generic type arguments and deduce the real return type from it.
     */
    public static Type resolveGenericReturnType(final ComponentDescriptor component, final Method method) {
        return resolveGenericType(component.getComponentType(), method.getDeclaringClass(), method.getGenericReturnType());
    }

    /**
     * Tries to deduce the actual runtime type for the generic type parameter.
     * @param componentType the class of a component
     * @param declaringType a base class of the given component class
     * @param typeParam the generic type parameter that needs to be resolved
     * @return the actual type
     */
    @SuppressWarnings({"UnstableApiUsage", "rawtypes", "unchecked"})
    private static Type resolveGenericType(final Class<?> componentType, final Class<?> declaringType, final Type typeParam) {
        if(declaringType.isAssignableFrom(componentType) && typeParam instanceof TypeVariable<?>) {
            final TypeToken actual = TypeToken.of(componentType);
            final TypeToken declared = actual.getSupertype(declaringType);
            final ParameterizedType parameterizedType = (ParameterizedType) declared.getType();
            final TypeVariable<?>[] declaredTypes = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
            final Type[] actualTypes = parameterizedType.getActualTypeArguments();
            final int typeIndex = Arrays.asList(declaredTypes).indexOf(typeParam);
            if (typeIndex >= 0) {
                return actualTypes[typeIndex];
            }
        }
        return typeParam;
    }
}
