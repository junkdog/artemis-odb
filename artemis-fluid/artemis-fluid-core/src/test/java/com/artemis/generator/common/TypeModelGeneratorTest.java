package com.artemis.generator.common;

import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.strategy.e.FieldProxyStrategy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class TypeModelGeneratorTest {

    private static final List<ComponentDescriptor> EMPTY_COMPONENT_LIST = new ArrayList<>();
    private static final List<FieldProxyStrategy> EMPTY_FIELD_PROXY_STRATEGY_LIST = new ArrayList<>();

    @Test(expected = IllegalArgumentException.class)
    public void should_abort_generation_if_no_generators() {
        new TypeModelGenerator().generate(new ArtemisModel(EMPTY_COMPONENT_LIST,EMPTY_FIELD_PROXY_STRATEGY_LIST));
    }
}