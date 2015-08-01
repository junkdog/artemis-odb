package com.artemis.injection;

import com.artemis.World;
import com.artemis.utils.reflect.Field;

import java.util.Map;

/**
 * Can inject arbitrary fields annotated with {@link com.artemis.annotations.Wire},
 * typically registered via registered via {@link com.artemis.WorldConfiguration#register}
 *
 * @author Snorre E. Brekke
 */
public class WiredFieldResolver implements FieldResolver, UseInjectionCache {
    private InjectionCache cache;

    private Map<String, Object> pojos;

    public WiredFieldResolver(Map<String, Object> pojos) {
        this.pojos = pojos;
    }

    @Override
    public void initialize(World world) {

    }

    @Override
    public Object resolve(Class<?> fieldType, Field field) {
        ClassType injectionType = cache.getFieldClassType(fieldType);
        CachedField cachedField = cache.getCachedField(field);

        if (injectionType == ClassType.CUSTOM) {
            if (cachedField.wireType == WireType.WIRE) {
                String key = cachedField.name;
                if ("".equals(key)) {
                    key = field.getType().getName();
                }

                return pojos.get(key);
            }
        }
        return null;
    }

    @Override
    public void setCache(InjectionCache cache) {
        this.cache = cache;
    }
}
