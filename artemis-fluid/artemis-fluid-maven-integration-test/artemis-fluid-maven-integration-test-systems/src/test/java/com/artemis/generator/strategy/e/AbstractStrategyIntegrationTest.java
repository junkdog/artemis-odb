package com.artemis.generator.strategy.e;

import com.artemis.*;

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
        return new EntityWorld(worldConfigurationBuilder.build());
    }
}
