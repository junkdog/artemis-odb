package com.squareup.javapoet;

import com.artemis.generator.model.type.TypeDescriptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends JavaPoet with TypeName resolver with support for {@link TypeDescriptor}.
 *
 * @see TypeDescriptor
 * @author Daan van Yperen
 */
public class PoetTypeNameResolver {

    public TypeName get(Type type) {
        return get(type, new LinkedHashMap<Type, TypeVariableName>());
    }

    private TypeName get(Type type, Map<Type, TypeVariableName> map) {
        if (type instanceof TypeDescriptor) {
            return ClassName.bestGuess(type.toString());
        }
        if (type instanceof ParameterizedType) {
            return getForParameterizedTypes((ParameterizedType) type, map);
        }
        return TypeName.get(type);
    }

    private ParameterizedTypeName getForParameterizedTypes(ParameterizedType type, Map<Type, TypeVariableName> map) {
        // like ParameterizedTypeName.get, but with TypeDescriptor support.
        final ClassName rawType = ClassName.get((Class<?>) type.getRawType());
        final ParameterizedType ownerType = (type.getOwnerType() instanceof ParameterizedType)
                && !java.lang.reflect.Modifier.isStatic(((Class<?>) type.getRawType()).getModifiers())
                ? (ParameterizedType) type.getOwnerType() : null;
        List<TypeName> typeArguments = list(type.getActualTypeArguments(), map);
        return (ownerType != null)
                ? getForParameterizedTypes(ownerType, map)
                .nestedClass(rawType.simpleName(), typeArguments)
                : new ParameterizedTypeName(null, rawType, typeArguments);

    }

    private List<TypeName> list(Type[] types, Map<Type, TypeVariableName> map) {
        // TypeName.list, but with TypeDescriptor support.
        List<TypeName> result = new ArrayList<>(types.length);
        for (Type type : types) {
            result.add(get(type, map));
        }
        return result;
    }
}
