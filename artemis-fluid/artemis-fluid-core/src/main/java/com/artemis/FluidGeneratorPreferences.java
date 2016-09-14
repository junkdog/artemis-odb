package com.artemis;

import com.artemis.annotations.Fluid;

/**
 * @author Daan van Yperen
 */
public class FluidGeneratorPreferences {

    /**
     * Generate fluid interface over returning parameterized values?
     */
    public boolean swallowGettersWithParameters = false;

    public FluidGeneratorPreferences() {
    }

    public void apply(Fluid fluid) {
        this.swallowGettersWithParameters = fluid.swallowGettersWithParameters();
    }
}
