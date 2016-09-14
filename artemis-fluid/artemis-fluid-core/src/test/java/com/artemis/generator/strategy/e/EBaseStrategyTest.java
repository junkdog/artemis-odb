package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.strategy.e.EBaseStrategy;
import com.artemis.generator.test.Flag;
import org.junit.Test;

/**
  * @author Daan van Yperen
 */
public class EBaseStrategyTest extends StrategyTest {

    @Test
    public void Should_add_create_int_instancer_method() {
        TypeModel model = applyStrategy(EBaseStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E E(int entityId)");
    }

    @Test
    public void Should_add_create_entity_instancer_method() {
        TypeModel model = applyStrategy(EBaseStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E E(com.artemis.Entity entity)");
    }
}