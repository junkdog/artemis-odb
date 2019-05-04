package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.ESubscription;
import com.artemis.annotations.All;
import com.artemis.component.Basic;
import com.artemis.managers.GroupManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ESubscriptionIntegrationTest extends AbstractStrategyIntegrationTest {

    @Test
    public void When_ESubscription_And_Aspect_Annotations_Should_inject() throws Exception {
        class TestSystem extends BaseSystem {

            @All(Basic.class)
            private ESubscription subscription;

            @Override
            protected void processSystem() {
                Assert.assertNotNull(subscription);
                Assert.assertNotNull(subscription.get());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }
}
