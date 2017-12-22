package com.artemis;

/**
 * Placeholder for utility classes that depend on E.
 * <p>
 * Serves as a placeholder for testing fluid utility classes. This file is never deployed with the utility classes,
 * instead {@link com.artemis.FluidGenerator} generates a user specific version. See artemis-fluid-core pom for
 * build details.
 *
 * @author Daan van Yperen
 */
public class E {
    public static E E(int entityId) {
        throw new RuntimeException("You shouldn't be using this class.");
    }

    public static E E(Entity entity) {
        throw new RuntimeException("You shouldn't be using this class.");
    }
}
