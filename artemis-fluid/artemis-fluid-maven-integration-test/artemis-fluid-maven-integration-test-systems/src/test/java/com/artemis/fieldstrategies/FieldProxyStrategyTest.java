package com.artemis.fieldstrategies;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.generator.strategy.e.AbstractStrategyIntegrationTest;
import org.junit.Test;

/**
 * @author Daan van Yperen
 * {@link ExperimentFieldProxyStrategy}
 */
public class FieldProxyStrategyTest extends AbstractStrategyIntegrationTest {
    @Test
    public void When_two_proxy_strategies_match_same_class_Should_follow_priority() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                E.E().proxyStrategyWorked();
            }
        }

        runFluidWorld(new TestSystem());
    }

}
