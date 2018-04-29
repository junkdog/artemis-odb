package com.artemis.systems;

import com.artemis.*;
import com.artemis.annotations.All;
import com.artemis.component.ComponentX;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class AspectAnnotationCompatibilityTest {

    @All(ComponentX.class)
    class System1 extends BaseEntitySystem {
        @Override
        protected void processSystem() {
        }
    }

    @All(ComponentX.class)
    class System2 extends EntityProcessingSystem {
        @Override
        protected void process(Entity e) {
        }
    }

    @All(ComponentX.class)
    class System3 extends EntitySystem {
        @Override
        protected void processSystem() {
        }
    }


    @All(ComponentX.class)
    class System4 extends IteratingSystem {
        @Override
        protected void process(int entityId) {
        }
    }

    @All(ComponentX.class)
    class System5 extends IntervalEntitySystem {
        public System5() {
            super(1);
        }

        @Override
        protected void processSystem() {
        }
    }

    @All(ComponentX.class)
    class System6 extends DelayedEntityProcessingSystem {
        @Override
        protected float getRemainingDelay(Entity e) {
            return 0;
        }

        @Override
        protected void processDelta(Entity e, float accumulatedDelta) {

        }

        @Override
        protected void processExpired(Entity e) {

        }
    }

    @All(ComponentX.class)
    class System7 extends DelayedIteratingSystem {
        @Override
        protected float getRemainingDelay(int entityId) {
            return 0;
        }

        @Override
        protected void processDelta(int entityId, float accumulatedDelta) {

        }

        @Override
        protected void processExpired(int entityId) {

        }
    }

    @Test
    public void When_annotation_applied_to_provided_systems_Should_assign_aspect_properly() {
        World world = new World(new WorldConfigurationBuilder().with(
                new System1(),
                new System2(),
                new System3(),
                new System4(),
                new System5(),
                new System6(),
                new System7()
                ).build());
        assertSystemHasAspect(world, System1.class);
        assertSystemHasAspect(world, System2.class);
        assertSystemHasAspect(world, System3.class);
        assertSystemHasAspect(world, System4.class);
        assertSystemHasAspect(world, System5.class);
        assertSystemHasAspect(world, System6.class);
        assertSystemHasAspect(world, System7.class);
    }

    private void assertSystemHasAspect(World world, Class<? extends BaseEntitySystem> type) {
        assertFalse(type.getClass().getSimpleName(),world.getSystem(type).getSubscription().getAspect().getAllSet().isEmpty());
    }
}