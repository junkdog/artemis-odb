package com.artemis;

/**
 * Plugin to enable fluid entity functionality in your world.
 *
 * This file is generated.
 *
 * For artemis-odb developers: Make sure you edit the file in  artemis-fluid-core-utility-sources, and not a
 * generated-sources version.
 */
public final class FluidEntityPlugin implements ArtemisPlugin {
    public void setup(WorldConfigurationBuilder b) {
        b.dependsOn(WorldConfigurationBuilder.Priority.HIGH, SuperMapper.class);
    }
}
