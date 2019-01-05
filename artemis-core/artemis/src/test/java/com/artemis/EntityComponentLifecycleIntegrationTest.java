package com.artemis;

import com.artemis.annotations.All;
import com.artemis.common.LifecycleComponent;
import com.artemis.common.NotExplicitlyDelayedComponent;
import com.artemis.component.ComponentX;
import com.artemis.utils.IntBag;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for consistent behaviour when entities are created or destroyed.
 *
 * <p>
 * Assumptions:
 * - Subscriptions do not update during processing.
 * - ComponentMappers immediately update during processing, with the exception of DelayedComponentRemoval.
 * <p>
 * <p>
 * Keep {@link ComponentMapper#remove(int)} {@link ComponentMapper#get(int)} {@link ComponentMapper#has(int)}
 * javadoc in sync with these tests!
 *
 * @author Daan van Yperen
 */
public class EntityComponentLifecycleIntegrationTest {

    // Entity lifecycle ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void When_creating_entity_Should_not_affect_subscriptions_before_processing() {
        execute(new LifecycleTestingSystem() {
            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    world.create();
                    Assert.assertEquals(0, allMembers());
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_creating_entity_Should_affect_subscriptions_after_processing() {
        execute(new LifecycleTestingSystem() {
            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    world.create();
                }
                if (timesProcessed == 1) {
                    Assert.assertEquals(1, allMembers());
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_removing_entity_Should_not_affect_subscriptions_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                }
                if (timesProcessed == 1) {
                    world.delete(entityId);
                    Assert.assertEquals(1, allMembers());
                    done = true;
                }
            }
        });
    }

    // @todo using deleteEntity, entity.deleteFroMWorld.

    @Test
    public void When_removing_entity_Should_affect_subscriptions_after_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                }
                if (timesProcessed == 1) {
                    world.delete(entityId);
                }
                if (timesProcessed == 2) {
                    Assert.assertEquals(0, allMembers());
                    done = true;
                }
            }
        });
    }

    // Component lifecycle ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void When_adding_component_Should_not_affect_subscriptions_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                }
                if (timesProcessed == 1) {
                    // entity is just created, should not affect subscription!
                    mLife.create(entityId);
                    Assert.assertEquals(0, lifeMembers());
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_adding_component_Should_affect_subscriptions_after_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                }
                if (timesProcessed == 1) {
                    mLife.create(entityId);
                }
                if (timesProcessed == 2) {
                    Assert.assertEquals(1, lifeMembers());
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_removing_component_Should_not_affect_subscriptions_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    mLife.create(entityId);
                    Assert.assertEquals(0, lifeMembers());
                }
                if (timesProcessed == 1) {
                    mLife.remove(entityId);
                    Assert.assertEquals(1, lifeMembers());
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_removing_component_Should_affect_subscriptions_after_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    mLife.create(entityId);
                    Assert.assertEquals(0, lifeMembers());
                }
                if (timesProcessed == 1) {
                    mLife.remove(entityId);
                }
                if (timesProcessed == 2) {
                    Assert.assertEquals(0, lifeMembers());
                    done = true;
                }
            }
        });
    }

    // Component Mapper ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void When_adding_component_mapper_Should_update_has_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    mLife.create(entityId);
                    Assert.assertTrue(mLife.has(entityId));
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_adding_component_mapper_Should_update_get_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    LifecycleComponent c = mLife.create(entityId);
                    Assert.assertEquals(c, mLife.get(entityId));
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_removing_component_mapper_Should_update_has_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    mLife.create(entityId);
                }
                if (timesProcessed == 1) {
                    mLife.remove(entityId);
                    Assert.assertFalse(mLife.has(entityId));
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_removing_component_mapper_Should_update_get_before_processing() {
        execute(new LifecycleTestingSystem() {
            private int entityId;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    entityId = world.create();
                    mLife.create(entityId);
                }
                if (timesProcessed == 1) {
                    mLife.remove(entityId);
                    Assert.assertNull(mLife.get(entityId));
                    done = true;
                }
            }
        });
    }


    // Edge cases ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void When_replacing_a_component_Should_not_call_subscriptions() {

        // when replacing a component within a system by succession of remove/create it should not trigger the subscription.

        execute(new LifecycleTestingSystem() {
            private int entityId;
            private int inserted;
            private int removed;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {

                    subLife.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                        @Override
                        public void inserted(IntBag entities) {
                            inserted++;
                        }

                        @Override
                        public void removed(IntBag entities) {
                            removed++;
                        }
                    });

                    entityId = world.create();
                    LifecycleComponent id1 = mLife.create(entityId);
                    mLife.remove(entityId);
                    Assert.assertNotEquals(id1, mLife.create(entityId));
                }
                if (timesProcessed == 1) {
                    Assert.assertEquals(1, inserted);
                    Assert.assertEquals(0, removed);
                    done = true;
                }
            }
        });
    }


    @Test
    public void When_replacing_a_delayed_component_Should_not_call_subscriptions() {

        // when replacing a deletion-delayed component within a system by succession of remove/create it
        // should not trigger the subscription.

        execute(new LifecycleTestingSystem() {
            private int entityId;
            private int inserted;
            private int removed;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    subDelayed.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                        @Override
                        public void inserted(IntBag entities) {
                            inserted++;
                        }

                        @Override
                        public void removed(IntBag entities) {
                            removed++;
                        }
                    });

                    entityId = world.create();
                    Component id1 = mDelayed.create(entityId);
                    mDelayed.remove(entityId);
                    Assert.assertNotEquals(id1, mDelayed.create(entityId));
                }
                if (timesProcessed == 1) {
                    Assert.assertEquals(1, inserted);
                    Assert.assertEquals(0, removed);
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_flash_in_the_pan_component_Should_not_trigger_subscription() {

        // Flash in the pan components should not reach subscriptions.

        execute(new LifecycleTestingSystem() {
            private int entityId;
            private int inserted;
            private int removed;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    subLife.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                        @Override
                        public void inserted(IntBag entities) {
                            inserted++;
                        }

                        @Override
                        public void removed(IntBag entities) {
                            removed++;
                        }
                    });
                    entityId = world.create();
                    LifecycleComponent id1 = mLife.create(entityId);
                    mLife.remove(entityId);
                }
                if (timesProcessed == 1) {
                    Assert.assertEquals(0, inserted);
                    Assert.assertEquals(0, removed);
                    done = true;
                }
            }
        });
    }

    @Test
    public void When_flash_in_the_pan_entity_Should_still_trigger_remove_subscription() {

        // Trigger 'remove' listeners for entities that did not survive birth.
        // Current behaviour. Bit weird but since this is probably fairly
        // uncommon usecase to spend much energy on just keeping the test for consistency sake.

        execute(new LifecycleTestingSystem() {
            private int entityId;
            private int inserted;
            private int removed;

            @Override
            protected void processSystem() {
                if (timesProcessed == 0) {
                    subAll.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                        @Override
                        public void inserted(IntBag entities) {
                            inserted++;
                        }

                        @Override
                        public void removed(IntBag entities) {
                            removed++;
                        }
                    });
                    entityId = world.create();
                    Entity entity = world.getEntity(entityId);
                    entity.deleteFromWorld();
                }
                if (timesProcessed == 1) {
                    Assert.assertEquals(0, inserted);
                    Assert.assertEquals(1, removed);
                    done = true;
                }
            }
        });
    }
    // Utility stuff ///////////////////////////////////////////////////////////////////////////////////////////

    private static abstract class LifecycleTestingSystem extends BaseEntitySystem {
        protected ComponentMapper<LifecycleComponent> mLife;
        protected ComponentMapper<ComponentX> mDelayed;
        public int timesProcessed;
        public boolean done = false;

        @All()
        EntitySubscription subAll;

        @All(LifecycleComponent.class)
        EntitySubscription subLife;

        @All(ComponentX.class)
        EntitySubscription subDelayed;

        public LifecycleTestingSystem(Class<? extends Component>... components) {
            super(Aspect.all(components));
        }

        @Override
        protected void initialize() {
            super.initialize();
        }

        public int allMembers() {
            return subAll.getEntities().size();
        }


        public int lifeMembers() {
            return subLife.getEntities().size();
        }

        public int delayedMembers() {
            return subDelayed.getEntities().size();
        }
    }

    // process given system twice.
    private void execute(LifecycleTestingSystem lifecycleTestingSystem) {
        World world = new World(new WorldConfigurationBuilder().with(lifecycleTestingSystem).build());
        world.process();
        lifecycleTestingSystem.timesProcessed++;
        world.process();
        lifecycleTestingSystem.timesProcessed++;
        world.process();

        Assert.assertTrue("Not all asserts reached, see LifecycleTestingSystem implementation.", lifecycleTestingSystem.done);
    }

}
