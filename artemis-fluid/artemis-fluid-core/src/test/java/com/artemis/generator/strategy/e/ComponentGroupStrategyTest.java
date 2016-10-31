package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.test.Flag;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class ComponentGroupStrategyTest extends StrategyTest {

    @Test
    public void Should_add_group__method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E group(java.lang.String group)");
    }


    @Test
    public void Should_add_groups_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E groups(java.lang.String[] groups)");
    }


    @Test
    public void Should_add_remove_group_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E removeGroup(java.lang.String group)");
    }


    @Test
    public void Should_add_remove_groups_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E removeGroups(java.lang.String[] groups)");
    }


    @Test
    public void Should_add_remove_all_groups_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"com.artemis.E removeGroups()");
    }


    @Test
    public void Should_add_get_all_groups_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"interface com.artemis.utils.ImmutableBag<class java.lang.String> groups()");
    }


    @Test
    public void Should_add_is_in_group_method() {
        TypeModel model = applyStrategy(ComponentGroupStrategy.class, Flag.class);
        assertHasMethod(model,"boolean isInGroup(java.lang.String group)");
    }
}