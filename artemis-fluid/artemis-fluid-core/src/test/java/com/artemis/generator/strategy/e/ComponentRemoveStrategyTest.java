package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.test.Flag;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class ComponentRemoveStrategyTest extends StrategyTest {

    @Test
    public void When_component_Should_add_remove_lifecycle_method() {
        TypeModel model = applyStrategy(ComponentRemoveStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E removeFlag()");
    }
}