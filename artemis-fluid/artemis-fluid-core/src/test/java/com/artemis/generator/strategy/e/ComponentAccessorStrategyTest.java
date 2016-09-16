package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.strategy.e.ComponentAccessorStrategy;
import com.artemis.generator.test.Flag;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentAccessorStrategyTest extends StrategyTest {

    @Test
    public void When_component_Should_add_create_lifecycle_method() {
        TypeModel model = applyStrategy(ComponentAccessorStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.generator.test.Flag getFlag()");
    }
}