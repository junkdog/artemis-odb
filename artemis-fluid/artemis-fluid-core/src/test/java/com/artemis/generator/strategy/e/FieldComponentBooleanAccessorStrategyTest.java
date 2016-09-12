package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import com.artemis.generator.test.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class FieldComponentBooleanAccessorStrategyTest  extends StrategyTest {

    @Test
    public void When_flag_component_Should_add_flag_methods() {
        TypeModel model = applyStrategy(FieldComponentBooleanAccessorStrategy.class, Flag.class);
        assertHasMethod(model,"boolean isFlag()");
        assertHasMethod(model,"com.artemis.E flag(boolean value)");
    }

    @Test
    public void When_single_public_field_Should_not_add_flag_methods() {
        TypeModel model = applyStrategy(FieldComponentBooleanAccessorStrategy.class, FlagWithPublicField.class);
        assertNoMethod(model,"boolean isFlagWithPublicField()");
        assertNoMethod(model,"com.artemis.E flagWithPublicField(boolean value)");
    }

    @Test
    public void When_single_private_field_Should_add_flag_methods() {
        TypeModel model = applyStrategy(FieldComponentBooleanAccessorStrategy.class, FlagWithPrivateField.class);
        assertHasMethod(model,"boolean isFlagWithPrivateField()");
        assertHasMethod(model,"com.artemis.E flagWithPrivateField(boolean value)");
    }


    @Test
    public void When_single_public_method_Should_not_add_flag_methods() {
        TypeModel model = applyStrategy(FieldComponentBooleanAccessorStrategy.class, FlagWithPublicMethod.class);
        assertNoMethod(model,"boolean isFlagWithPublicMethod()");
        assertNoMethod(model,"com.artemis.E flagWithPublicMethod(boolean value)");
    }

    @Test
    public void When_single_private_method_Should_add_flag_methods() {
        TypeModel model = applyStrategy(FieldComponentBooleanAccessorStrategy.class, FlagWithPrivateMethod.class);
        assertHasMethod(model,"boolean isFlagWithPrivateMethod()");
        assertHasMethod(model,"com.artemis.E flagWithPrivateMethod(boolean value)");
    }
}