package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.injection.*;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author Snorre E. Brekke
 */
public class FieldHandlerTest {
    @Test
    public void default_field_handler_injects_correct_core_fields() {
        WorldConfiguration worldConfiguration = new WorldConfiguration()
                .setSystem(new SomeSystem())
                .setSystem(new SomeManager())
                .register(new Object());
        World world = new World(worldConfiguration);

        ObjectWithCoreFields withCoreFields = new ObjectWithCoreFields();
        world.inject(withCoreFields);

        assertNotNull(withCoreFields.cm);
        assertNotNull(withCoreFields.system);
        assertNotNull(withCoreFields.manager);
        assertNotNull(withCoreFields.injectedObject);

        assertNull(withCoreFields.notInjected);
    }


    @Test
    public void custom_field_resolver_injects_fields() {
        FieldHandler fieldHandler = new FieldHandler(new InjectionCache());
        fieldHandler.addFieldResolver(new AllFieldsResolver());

        Injector injector = new CachedInjector().setFieldHandler(fieldHandler);
        World world = new World(new WorldConfiguration().setInjector(injector));

        ObjectWithNoArgsConstructorFields objectsInjected = new ObjectWithNoArgsConstructorFields();
        world.inject(objectsInjected);

        assertNotNull(objectsInjected.object);
        assertNotNull(objectsInjected.string);
    }

    @Test
    public void default_field_handler_injects_world_fields() {
        WorldConfiguration worldConfiguration = new WorldConfiguration();
        World world = new World(worldConfiguration);

        CustomObjectWithWorldField withWorldField = new CustomObjectWithWorldField();

        world.inject(withWorldField);

        assertNotNull(withWorldField.world);
    }

    private static class CustomObjectWithWorldField {
        private World world;
    }

    private static class ObjectWithNoArgsConstructorFields{
        private Object object;
        private String string;
    }

    private static class ObjectWithCoreFields{
        private ComponentMapper<SomeComponent> cm;
        private SomeSystem system;
        private SomeManager manager;
        @Wire private Object injectedObject;
        private Object notInjected;
    }

    public static class SomeComponent extends Component {}

    private static class SomeSystem extends BaseSystem{
        @Override
        protected void processSystem() {

        }
    }

    private static class SomeManager extends Manager{

    }

    private static class AllFieldsResolver implements FieldResolver {
        @Override
        public void initialize(World world) {
        }

        @Override
        public Object resolve(Object target, Class<?> fieldType, Field field) {
            try {
                return ClassReflection.newInstance(fieldType);
            } catch (ReflectionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
