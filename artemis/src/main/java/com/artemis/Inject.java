package com.artemis;

/**
 *
 */
public interface Inject {

    void initialize(World world, WorldConfiguration config);

    void update();

    void inject(Object target) throws RuntimeException;

    boolean injectionSupported(Object target);

}
