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
    private String prefixComponentGetter = "get";

    public FluidGeneratorPreferences() {
    }

    public void apply(Fluid fluid) {
        this.swallowGettersWithParameters = fluid.swallowGettersWithParameters();
    }

    /** Get prefix for component getters. Default "get". */
    public String getPrefixComponentGetter() {
        return prefixComponentGetter;
    }

    /** Set prefix for component setters. Default "get". */
    public void setPrefixComponentGetter(String prefixComponentGetter) {
        this.prefixComponentGetter = prefixComponentGetter;
    }

    /** Copy settings from source to this. */
    public void mirror(FluidGeneratorPreferences source) {
        this.swallowGettersWithParameters = source.swallowGettersWithParameters;
        this.prefixComponentGetter = source.prefixComponentGetter;
    }

    public boolean isSwallowGettersWithParameters() {
        return swallowGettersWithParameters;
    }

    public void setSwallowGettersWithParameters(boolean swallowGettersWithParameters) {
        this.swallowGettersWithParameters = swallowGettersWithParameters;
    }
}
