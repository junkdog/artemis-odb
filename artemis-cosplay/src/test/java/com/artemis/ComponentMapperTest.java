package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.systems.EntityProcessingSystem;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */

/**
 * @author Daan van Yperen
 */
public class ComponentMapperTest {

    public static class Pos extends Component {
        public int x, y;

        public Pos() {
        }

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class TestMarker extends Component {
    }

    @Wire
    public class BasicSystem extends EntityProcessingSystem {

        public BasicSystem() {
            super(Aspect.all(TestMarker.class));
        }

        protected ComponentMapper<Pos> mPos;

        @Override
        protected void process(Entity e) {
        }
    }

    @Test
    public void create_if_missing_should_create_new_component() {

        @Wire(injectInherited = true)
        class TestSystemA extends BasicSystem {
            @Override
            protected void process(Entity e) {
                Pos c = mPos.create(e);
                Assert.assertNotNull(c);
            }
        }

        createAndProcessEntityWorld(new TestSystemA());
    }

    @Test
    public void mappers_are_per_type_per_world() {
        EntityWorld w1 = new EntityWorld();
        EntityWorld w2 = new EntityWorld();

        assertNotSame(w1.getMapper(ComponentX.class), w2.getMapper(ComponentX.class));
        assertNotSame(w1.getMapper(ComponentX.class), w1.getMapper(ComponentY.class));
        assertSame(w1.getMapper(ComponentX.class), w1.getMapper(ComponentX.class));
    }

    @Test
    public void create_if_exists_should_recycle_existing_component() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                Pos c1 = mPos.create(e);
                Pos c2 = mPos.create(e);
                Assert.assertEquals(c1, c2);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void set_if_exists_should_recycle_existing_component() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                Pos c1 = mPos.create(e);
                Pos c2 = mPos.set(e, true);
                Assert.assertEquals(c1, c2);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void set_if_exists_should_remove_component() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                mPos.create(e);
                mPos.set(e, false);
                Assert.assertFalse(mPos.has(e));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void remove_if_exists_should_remove_component() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                mPos.create(e);
                mPos.remove(e);
                Assert.assertFalse(mPos.has(e));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void remove_if_missing_should_not_explode() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                mPos.remove(e);
                Assert.assertFalse(mPos.has(e));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void set_if_missing_should_not_explode() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                mPos.set(e, false);
                Assert.assertFalse(mPos.has(e));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void getsafe_with_fallback_should_return_fallback_when_component_missing() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {

            private Pos fallbackPos = new Pos(10, 10);

            @Override
            protected void process(Entity e) {
                Assert.assertEquals("expected to return fallback.", fallbackPos, mPos.getSafe(e, fallbackPos));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void getsafe_with_fallback_should_return_component_when_available() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {

            private Pos fallbackPos = new Pos(10, 10);

            @Override
            protected void process(Entity e) {
                final Pos pos = mPos.create(e);
                Assert.assertEquals("expected to return component.", pos, mPos.getSafe(e, fallbackPos));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void getsafe_with_null_fallback_should_return_null() {

        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {

            private Pos fallbackPos = new Pos(10, 10);

            @Override
            protected void process(Entity e) {
                Assert.assertNull(mPos.getSafe(e, null));
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    protected void createAndProcessEntityWorld(BaseSystem system) {
        final EntityWorld world = new EntityWorld(new WorldConfiguration().setSystem(system));
        world.createEntity().edit().create(TestMarker.class);
        world.process();
    }

    protected void createAndProcessBasicWorld(BaseSystem system) {
        final World world = new World(new WorldConfiguration().setSystem(system));
        world.edit(world.create()).create(TestMarker.class);
        world.process();
    }

    @Test
    public void create_right_after_entity_creation_should_not_throw_exception() {
        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                final int t1 = world.create();
                Pos c1 = mPos.create(t1);
                Assert.assertNotNull(c1);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }


    @Test
    public void remove_right_after_entity_creation_should_not_throw_exception() {
        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                final int t1 = world.create();
                mPos.remove(t1);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test
    public void create_by_id_right_after_entity_creation_should_not_throw_exception() {
        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                final int t1 = world.create();
                Pos c1 = mPos.create(t1);
                Assert.assertNotNull(c1);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }


    @Test
    public void remove_by_id_right_after_entity_creation_should_not_throw_exception() {
        @Wire(injectInherited = true)
        class TestSystem extends BasicSystem {
            @Override
            protected void process(Entity e) {
                final int t1 = world.create();
                mPos.remove(t1);
            }
        }
        createAndProcessEntityWorld(new TestSystem());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_get_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.get(e);
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_has_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.has(e);
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_getSafe_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.getSafe(e, null);
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_set_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.set(e, true);
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_remove_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.remove(e);
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void When_calling_create_by_reference_method_Throw_unsupported_operation_exception() {
        createAndProcessBasicWorld(new TestUnsupportedOperationSystem() {
            @Override
            protected void processSystem() {
                mPos.create(e);
            }
        });
    }

    abstract class TestUnsupportedOperationSystem extends BaseSystem {
        protected ComponentMapper<Pos> mPos;
        protected Entity e = new Entity(world, 1);
    }

}
