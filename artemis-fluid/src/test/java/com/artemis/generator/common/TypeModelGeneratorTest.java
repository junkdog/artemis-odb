package com.artemis.generator.common;

import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class TypeModelGeneratorTest {

    public static final List<ComponentDescriptor> EMPTY_COMPONENT_LIST = new ArrayList<ComponentDescriptor>();

    @Test(expected = IllegalArgumentException.class)
    public void should_abort_generation_if_no_generators() {
        new TypeModelGenerator().generate(new ArtemisModel(EMPTY_COMPONENT_LIST));
    }
}