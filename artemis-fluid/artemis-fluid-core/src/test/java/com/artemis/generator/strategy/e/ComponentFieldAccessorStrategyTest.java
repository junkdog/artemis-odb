package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentFieldAccessorStrategyTest extends StrategyTest {

    @Test
    public void When_non_public_method_Should_not_expose() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertNoMethod(model,"long proofClearPRO()");
        assertNoMethod(model,"long proofClearP()");
        assertNoMethod(model,"long proofClearT()");
    }

    @Test
    public void When_interface_on_component_Should_ignore() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertNoMethod(model,"java.lang.Object proofPancake(java.lang.Object p0)");
    }

    @Test
    public void When_public_field_Should_expose_as_getter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertHasMethod(model,"int proofPub()");
    }

    @Test
    public void When_non_public_field_Should_not_expose_as_getter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertNoMethod(model,"int proofPri()");
        assertNoMethod(model,"int proofProt()");
        assertNoMethod(model,"int proofUndef()");
    }

    @Test
    public void When_public_field_Should_expose_as_setter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertHasMethod(model,"com.artemis.E proofPub(int pub)");
    }

    @Test
    public void When_non_public_field_Should_not_expose_as_setter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertNoMethod(model,"com.artemis.E proofPri(int pri)");
        assertNoMethod(model,"com.artemis.E proofProt(int prot)");
        assertNoMethod(model,"com.artemis.E proofUndef(int undef)");
    }

    @Test
    public void When_public_field_with_parameterized_Type_Should_expose_as_parameterized_getter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertHasMethod(model,"java.util.List<java.lang.Object> proofGen()");
    }

    @Test
    public void When_public_field_with_parameterized_Type_Should_expose_as_parameterized_setter_method() {
        TypeModel model = applyStrategy(ComponentFieldAccessorStrategy.class, Proof.class);
        assertHasMethod(model, "com.artemis.E proofGen(java.util.List<java.lang.Object> gen)");
    }
}