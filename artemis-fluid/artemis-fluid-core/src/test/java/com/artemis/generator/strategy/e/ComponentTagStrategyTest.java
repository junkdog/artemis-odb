package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.test.Flag;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class ComponentTagStrategyTest extends StrategyTest {

    @Test
    public void Should_add_tag_setter_method() {
        TypeModel model = applyStrategy(ComponentTagStrategy.class);
        assertHasMethod(model,"com.artemis.E tag(java.lang.String tag)");
    }

    @Test
    public void Should_add_tag_getter_method() {
        TypeModel model = applyStrategy(ComponentTagStrategy.class);
        assertHasMethod(model,"java.lang.String tag()");
    }
}