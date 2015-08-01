package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.injection.CachedInjector;
import com.artemis.injection.FieldHandler;
import com.artemis.injection.FieldResolver;
import com.artemis.injection.InjectionCache;
import com.artemis.injection.Injector;
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
    public void default_field_handler_injects_correct_core_fields() throws Exception {
        WorldConfiguration worldConfiguration = new WorldConfiguration()
                .setSystem(new SomeSystem())
                .setManager(new SomeManager())
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
    public void custom_field_handler_should_not_inject_wire_fields() throws Exception {
        FieldHandler fieldHandler = new FieldHandler(new InjectionCache());
        Injector injector = new CachedInjector().setFieldHandler(fieldHandler);
        WorldConfiguration worldConfiguration = new WorldConfiguration()
                .setInjector(injector)
                .setSystem(new SomeSystem())
                .setManager(new SomeManager())
                .register(new Object());
        World world = new World(worldConfiguration);

        ObjectWithCoreFields withCoreFields = new ObjectWithCoreFields();
        world.inject(withCoreFields);

        assertNotNull(withCoreFields.cm);
        assertNotNull(withCoreFields.system);
        assertNotNull(withCoreFields.manager);

        assertNull(withCoreFields.injectedObject);
        assertNull(withCoreFields.notInjected);
    }


    @Test
    public void custom_field_resolver_injects_fields() throws Exception {
        FieldHandler fieldHandler = new FieldHandler(new InjectionCache());
        fieldHandler.addFieldResolver(new AllFieldsResolver());

        Injector injector = new CachedInjector().setFieldHandler(fieldHandler);
        World world = new World(new WorldConfiguration().setInjector(injector));

        ObjectWithNoArgsConstructorFields objectsInjected = new ObjectWithNoArgsConstructorFields();
        world.inject(objectsInjected);

        assertNotNull(objectsInjected.object);
        assertNotNull(objectsInjected.string);
    }

    @Wire
    private static class ObjectWithNoArgsConstructorFields{
        private Object object;
        private String string;
    }

    @Wire
    private static class ObjectWithCoreFields{
        private ComponentMapper<SomeComponent> cm;
        private SomeSystem system;
        private SomeManager manager;
        @Wire
        private Object injectedObject;
        private Object notInjected;
    }

    private static class SomeComponent extends Component{

    }

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
        public Object resolve(Class<?> fieldType, Field field) {
            try {
                return ClassReflection.newInstance(fieldType);
            } catch (ReflectionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
