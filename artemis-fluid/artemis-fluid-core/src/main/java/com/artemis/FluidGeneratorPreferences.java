package com.artemis;

import com.artemis.annotations.Fluid;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> excludeFromClasspath = new ArrayList<String>();
    private boolean excludeFromGeneration = false;

    {
        excludeFromClasspath.add("-sources.jar"); // exclude sources
        excludeFromClasspath.add("gwt-user-"); // exclude gwt.
    }

    public FluidGeneratorPreferences() {
    }

    public void apply(Fluid fluid) {
        this.swallowGettersWithParameters = fluid.swallowGettersWithParameters();
        this.excludeFromGeneration = fluid.exclude();
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
        this.excludeFromClasspath = new ArrayList<String>(source.excludeFromClasspath);
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

    public List<String> getExcludeFromClasspath() {
        return excludeFromClasspath;
    }

    public void setExcludeFromClasspath(List<String> excludeFromClasspath) {
        this.excludeFromClasspath = excludeFromClasspath;
    }

    public boolean matchesIgnoredClasspath(String element) {
        for (String segment : excludeFromClasspath) {
            if ( element.contains(segment)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExcludeFromGeneration() {
        return excludeFromGeneration;
    }

    public void setExcludeFromGeneration(boolean excludeFromGeneration) {
        this.excludeFromGeneration = excludeFromGeneration;
    }
}
