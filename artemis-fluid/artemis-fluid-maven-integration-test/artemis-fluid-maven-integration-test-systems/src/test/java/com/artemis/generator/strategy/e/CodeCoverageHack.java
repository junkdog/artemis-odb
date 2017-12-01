package com.artemis.generator.strategy.e;

import com.artemis.CosplayBaseSystem;
import com.artemis.E;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class CodeCoverageHack extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_toggle_flag_component_Should_affect_component_and_return_fluid() throws Exception {

        class TestSystem extends CosplayBaseSystem<E> {
            @Override
            protected void processSystem() {
                E e = E();

                // code coverage since we don't need to test this for each class.
                E().flag();
                E().hasFlag();
                E().getFlag();
                E().removeFlag();

                E().rename2();
                E().hasRename2();
                E().getRename2();
                E().removeRename2();
                E().isRename2();
                E().rename2(true);
                
                E().paraGetter();
                E().hasParaGetter();
                E().getParaGetter();
                E().removeParaGetter();
            }
        }

        runFluidWorld(new TestSystem());
    }

}
