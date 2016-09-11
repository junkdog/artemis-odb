package com.artemis.generator.strategy;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.e.EBaseStrategy;
import com.artemis.generator.test.Flag;
import org.junit.Test;

/**
  * @author Daan van Yperen
 */
public class EInstancerStrategyTest extends StrategyTest {

    @Test
    public void Should_add_create_instancer_method() {
        TypeModel model = applyStrategy(EBaseStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E E()");
    }
}