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
                .dependsOn(FluidEntityPlugin.class);
        worldConfigurationBuilder.with(systems);
        return new World(worldConfigurationBuilder.build());
    }
}
