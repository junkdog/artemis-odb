package com.artemis.generator.common;

import com.artemis.Component;
import com.artemis.generator.model.ComponentDescriptor;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Daan on 10-9-2016.
 */
public class BuilderModelGeneratorTest {

    public static final List<ComponentDescriptor> EMPTY_COMPONENT_LIST = new ArrayList<ComponentDescriptor>();

    @Test(expected = IllegalArgumentException.class)
    public void should_abort_generation_if_no_generators() {
        new BuilderModelGenerator().generate(EMPTY_COMPONENT_LIST);
    }
}