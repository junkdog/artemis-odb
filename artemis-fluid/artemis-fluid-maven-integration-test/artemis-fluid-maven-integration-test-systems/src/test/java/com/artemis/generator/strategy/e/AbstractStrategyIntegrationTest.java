package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;

/**
 * @author Daan van Yperen
 */
public abstract class AbstractStrategyIntegrationTest {

    void runFluidWorld(BaseSystem system) {
        createFluidWorld(system).process();
    }

    World createFluidWorld(BaseSystem system) {
        return new World(new WorldConfigurationBuilder()
                .with(new SuperMapper(), system).build());
    }
}
