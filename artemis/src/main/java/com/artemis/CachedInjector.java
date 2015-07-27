package com.artemis;

import com.artemis.annotations.Mapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Injects {@link ComponentMapper}, {@link BaseSystem} and {@link Manager} types into systems and
 * managers. Can also inject arbitrary types if registered through {@link WorldConfiguration#register}.
 *
 * Caches all type-information.
 */
final class CachedInjector implements Inject {
    private World world;

    private Map<Class<?>, Class<?>> systems;
    private Map<Class<?>, Class<?>> managers;
    private Map<String, Object> pojos;

    private static final Map<Class<?>, CachedClass> classCache = new IdentityHashMap<Class<?>, CachedClass>();
    private static final Map<Class<?>, ClassType> fieldClassTypeCache = new IdentityHashMap<Class<?>, ClassType>();
    private static final Map<Field, CachedField> namedWireCache = new IdentityHashMap<Field, CachedField>();
    private static final Map<Field, Class<? extends Component>> mapperTypeCache = new IdentityHashMap<Field, Class<? extends Component>>();

    @Override
    public void initialize(World world, WorldConfiguration config) {
        this.world = world;
        systems = new IdentityHashMap<Class<?>, Class<?>>();
        managers = new IdentityHashMap<Class<?>, Class<?>>();
        pojos = new HashMap<String, Object>(config.injectables);
    }

    @Override
    public boolean injectionSupported(Object target) {
        try {
            CachedClass cachedClass = getCachedClass(target.getClass());
            return cachedClass.wireType == WireType.WIRE;
        } catch (ReflectionException e) {
            throw new MundaneWireException("Error while wiring", e);
        }
    }

    @Override
    public void update() {
        for (BaseSystem es : world.getSystems()) {
            Class<?> origin = es.getClass();
            Class<?> clazz = origin;
            do {
                systems.put(clazz, origin);
            } while ((clazz = clazz.getSuperclass()) != Object.class);
        }

        for (Manager manager : world.getManagers()) {
            Class<?> origin = manager.getClass();
            Class<?> clazz = origin;
            do {
                managers.put(clazz, origin);
            } while ((clazz = clazz.getSuperclass()) != Object.class);
        }
    }

    @Override
    public void inject(Object target) throws RuntimeException {
        try {
            Class<?> clazz = target.getClass();

            CachedClass cachedClass = getCachedClass(clazz);

            if (cachedClass.wireType == WireType.WIRE) {
                injectValidFields(target, cachedClass);
            } else {
                injectAnnotatedFields(target, cachedClass);
            }
        } catch (ReflectionException e) {
            throw new MundaneWireException("Error while wiring", e);
        }
    }

    private CachedClass getCachedClass(Class<?> clazz) throws ReflectionException {
        CachedClass cachedClass = classCache.get(clazz);
        if (cachedClass == null) {
            cachedClass = new CachedClass(clazz);
            cachedClass.wireType = ClassReflection.isAnnotationPresent(clazz, Wire.class) ?
                                WireType.WIRE :
                                WireType.IGNORED;

            if (cachedClass.wireType == WireType.WIRE) {
                Wire wireAnnotation = ClassReflection.getAnnotation(clazz, Wire.class);
                cachedClass.wireAnnotation = wireAnnotation;
                cachedClass.failOnNull = wireAnnotation.failOnNull();
                cachedClass.injectInherited = wireAnnotation.injectInherited();
            }
            classCache.put(clazz, cachedClass);
        }
        return cachedClass;
    }

    private void injectValidFields(Object target, CachedClass cachedClass)
            throws ReflectionException {
        Field[] declaredFields = getAllFields(cachedClass);
        for (int i = 0, s = declaredFields.length; s > i; i++) {
            injectField(target, declaredFields[i], cachedClass.failOnNull);
        }
    }

    private Field[] getAllFields(CachedClass cachedClass) {
        Field[] declaredFields = cachedClass.allFields;
        if (declaredFields == null) {
            List<Field> fieldList = new ArrayList<Field>();
            Class<?> clazz = cachedClass.clazz;
            collectDeclaredFields(fieldList, clazz);

            while (cachedClass.injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
                collectDeclaredFields(fieldList, clazz);
            }
            declaredFields = fieldList.toArray(new Field[fieldList.size()]);
        }
        return declaredFields;
    }

    private void collectDeclaredFields(List<Field> fieldList, Class<?> clazz) {
        Field[] classFields = ClassReflection.getDeclaredFields(clazz);
        for (int i = 0; i < classFields.length; i++) {
            fieldList.add(classFields[i]);
        }
    }

    private void injectAnnotatedFields(Object target, CachedClass cachedClass)
            throws ReflectionException {
        injectClass(target, cachedClass);
    }

    @SuppressWarnings("deprecation")
    private void injectClass(Object target, CachedClass cachedClass) throws ReflectionException {
        Field[] declaredFields = cachedClass.allFields;
        if (declaredFields == null) {
            cachedClass.allFields = declaredFields = ClassReflection.getDeclaredFields(cachedClass.clazz);
        }
        for (int i = 0, s = declaredFields.length; s > i; i++) {
            Field field = declaredFields[i];
            if (field.isAnnotationPresent(Mapper.class) || field.isAnnotationPresent(Wire.class)) {
                injectField(target, field, field.isAnnotationPresent(Wire.class));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void injectField(Object target, Field field, boolean failOnNotInjected)
            throws ReflectionException {

        field.setAccessible(true);

        Class<?> fieldType;
        try {
            fieldType = field.getType();
        } catch (RuntimeException ignore) {
            // Swallow exception caused by missing typedata on gwt platfString.format("Failed to inject %s into %s:
            // %s not registered with world.")orm.
            // @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for
            // users failing to add systems/components to gwt reflection inclusion config.
            return;
        }

        ClassType injectionType = getFieldClassType(fieldType);

        if (injectionType == ClassType.MAPPER) {
            injectMapper(target, field, failOnNotInjected);
        } else if (injectionType == ClassType.SYSTEM) {
            injectSystem(target, field, failOnNotInjected, fieldType);
        } else if (injectionType == ClassType.MANAGER) {
            injectManager(target, field, failOnNotInjected, fieldType);
        } else if (injectionType == ClassType.FACTORY) {
            injectFactory(target, field, failOnNotInjected, fieldType);
        } if (injectionType == ClassType.CUSTOM) {
            injectCustomType(target, field);
        }
    }

    @SuppressWarnings("unchecked")
    private void injectMapper(Object target, Field field, boolean failOnNotInjected) throws ReflectionException {
        Class<? extends Component> mapperType = mapperTypeCache.get(field);
        if (mapperType == null) {
            mapperType = field.getElementType(0);
            mapperTypeCache.put(field, mapperType);
        }
        ComponentMapper<?> mapper = world.getMapper(mapperType);
        if (failOnNotInjected && mapper == null) {
            throw onFailedInjection("ComponentMapper", field);
        }

        field.set(target, mapper);
    }

    @SuppressWarnings("unchecked")
    private void injectSystem(Object target, Field field, boolean failOnNotInjected,
                              Class<?> fieldType) throws ReflectionException {
        BaseSystem system = world.getSystem((Class<BaseSystem>) systems.get(fieldType));
        if (failOnNotInjected && system == null) {
            throw onFailedInjection("BaseSystem", field);
        }

        field.set(target, system);
    }

    @SuppressWarnings("unchecked")
    private void injectManager(Object target, Field field, boolean failOnNotInjected,
                               Class<?> fieldType) throws ReflectionException {
        Manager manager = world.getManager((Class<Manager>) managers.get(fieldType));
        if (failOnNotInjected && manager == null) {
            throw onFailedInjection("Manager", field);
        }

        field.set(target, manager);
    }

    private void injectFactory(Object target, Field field, boolean failOnNotInjected,
                               Class<?> fieldType) throws ReflectionException {
        EntityFactory<?> factory = (EntityFactory<?>) world.createFactory(fieldType);
        if (failOnNotInjected && factory == null) {
            throw onFailedInjection("EntityFactory", field);
        }

        field.set(target, factory);
    }

    private void injectCustomType(Object target, Field field) throws ReflectionException {
        CachedField cachedField = namedWireCache.get(field);
        if (cachedField == null) {
            if (field.isAnnotationPresent(Wire.class)) {
                final Wire wire = field.getAnnotation(Wire.class);
                cachedField = new CachedField(true, wire.name());
            } else {
                cachedField = new CachedField(false, null);
            }
            namedWireCache.put(field, cachedField);
        }

        if (cachedField.wire) {
            String key = cachedField.name;
            if ("".equals(key)) {
                key = field.getType().getName();
            }

            if (pojos.containsKey(key)) {
                field.set(target, pojos.get(key));
            }
        }
    }

    private ClassType getFieldClassType(Class<?> fieldType) {
        ClassType injectionType = fieldClassTypeCache.get(fieldType);
        if (injectionType == null) {
            if (ClassReflection.isAssignableFrom(ComponentMapper.class, fieldType)) {
                injectionType = ClassType.MAPPER;
            } else if(ClassReflection.isAssignableFrom(BaseSystem.class, fieldType)) {
                injectionType = ClassType.SYSTEM;
            } else if(ClassReflection.isAssignableFrom(Manager.class, fieldType)) {
                injectionType = ClassType.MANAGER;
            } else if(ClassReflection.isAssignableFrom(EntityFactory.class, fieldType)){
                injectionType = ClassType.FACTORY;
            } else {
                injectionType = ClassType.CUSTOM;
            }
            fieldClassTypeCache.put(fieldType, injectionType);
        }
        return injectionType;
    }

    private MundaneWireException onFailedInjection(String typeName, Field failedInjection) {
        String error = new StringBuilder()
                .append("Failed to inject ").append(failedInjection.getType().getName())
                .append(" into ").append(failedInjection.getDeclaringClass().getName()).append(": ")
                .append(typeName).append(" not registered with world.")
                .toString();

        return new MundaneWireException(error);
    }

    private enum ClassType {
        MAPPER, SYSTEM, MANAGER, FACTORY, CUSTOM
    }

    private enum WireType {
        WIRE, IGNORED
    }

    private static class CachedClass {
        public CachedClass(Class<?> clazz) {
            this.clazz = clazz;
        }
        Class<?> clazz;
        Field[] allFields;
        Wire wireAnnotation;
        boolean injectInherited;
        boolean failOnNull;
        WireType wireType;
    }

    private static class CachedField{
        public CachedField(boolean wire, String name) {
            this.wire = wire;
            this.name = name;
        }

        boolean wire;
        String name;
    }
}
