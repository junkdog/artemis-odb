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
    private String prefixComponentCreate = "";
    private String prefixComponentHas = "has";
    private boolean generateTagMethods = true;
    private boolean generateGroupMethods = true;
    private String prefixComponentRemove = "remove";
    private boolean generateBooleanComponentAccessors = true;

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
        this.prefixComponentCreate = source.prefixComponentCreate;
        this.prefixComponentHas = source.prefixComponentHas;
        this.prefixComponentRemove = source.prefixComponentRemove;

        this.generateTagMethods = source.generateTagMethods;
        this.generateGroupMethods = source.generateGroupMethods;
        this.generateBooleanComponentAccessors = source.generateBooleanComponentAccessors;
    }

    public boolean isSwallowGettersWithParameters() {
        return swallowGettersWithParameters;
    }

    public void setSwallowGettersWithParameters(boolean swallowGettersWithParameters) {
        this.swallowGettersWithParameters = swallowGettersWithParameters;
    }

    public String getPrefixComponentCreate() {
        return prefixComponentCreate;
    }

    public void setPrefixComponentCreate(String prefixComponentCreate) {
        this.prefixComponentCreate = prefixComponentCreate;
    }

    public String getPrefixComponentHas() {
        return prefixComponentHas;
    }

    public void setPrefixComponentHas(String prefixComponentHas) {
        this.prefixComponentHas = prefixComponentHas;
    }

    public boolean isGenerateTagMethods() {
        return generateTagMethods;
    }

    public void setGenerateTagMethods(boolean generateTagMethods) {
        this.generateTagMethods = generateTagMethods;
    }

    public boolean isGenerateGroupMethods() {
        return generateGroupMethods;
    }

    public void setGenerateGroupMethods(boolean generateGroupMethods) {
        this.generateGroupMethods = generateGroupMethods;
    }

    public String getPrefixComponentRemove() {
        return prefixComponentRemove;
    }

    public void setPrefixComponentRemove(String prefixComponentRemove) {
        this.prefixComponentRemove = prefixComponentRemove;
    }

    public boolean isGenerateBooleanComponentAccessors() {
        return generateBooleanComponentAccessors;
    }

    public void setGenerateBooleanComponentAccessors(boolean generateBooleanComponentAccessors) {
        this.generateBooleanComponentAccessors = generateBooleanComponentAccessors;
    }
}
