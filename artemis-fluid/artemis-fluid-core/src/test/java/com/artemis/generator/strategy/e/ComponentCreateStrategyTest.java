package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.strategy.e.ComponentCreateStrategy;
import com.artemis.generator.test.Flag;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentCreateStrategyTest extends StrategyTest {

    @Test
    public void When_component_Should_add_create_lifecycle_method() {
        TypeModel model = applyStrategy(ComponentCreateStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E flag()");
    }
}