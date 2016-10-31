package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;

/**
 * @author Daan van Yperen
 */
public abstract class AbstractStrategyIntegrationTest {

    protected void runFluidWorld(BaseSystem... system) {
        createFluidWorld(system).process();
    }

    protected World createFluidWorld(BaseSystem... systems) {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder()
                .with(new SuperMapper());
        worldConfigurationBuilder.with(systems);
        return new World(worldConfigurationBuilder.build());
    }
}
