package com.artemis.generator.strategy.e;

import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.StrategyTest;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentMethodProxyStrategyTest extends StrategyTest {

    @Test
    public void When_public_return_value_method_Should_expose_as_getter_method() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertHasMethod(model,"long proofClear2()");
    }

    @Test
    public void When_explicitly_excluded_method_Should_explicitly_exclude() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertNoMethod(model,"long exclude()");
    }

    @Test
    public void When_public_getter_method_Should_expose_as_getter_method_without_get_prefix() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertHasMethod(model,"long proofDepth()");
    }

    @Test
    public void When_public_setter_method_Should_expose_as_setter_method_without_set_prefix() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertHasMethod(model,"com.artemis.E proofDepth(long p0)");
    }

    @Test
    public void When_public_void_multi_parameterized_method_Should_expose_as_multi_parameter_method() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertHasMethod(model,"com.artemis.E proof(int p0,int p1,int p2)");
    }

    @Test
    public void When_public_void_parameterized_method_with_other_return_type_Should_not_expose() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertNoMethod(model,"java.lang.int proofStrange(com.artemis.generator.strategy.e.Proof p0)");
    }

    @Test
    public void When_public_void_parameterized_method_with_own_component_type_as_return_type_Should_expose_as_setter() {
        TypeModel model = applyStrategy(ComponentMethodProxyStrategy.class, Proof.class);
        assertHasMethod(model,"com.artemis.generator.strategy.e.Proof proofFluid(com.artemis.generator.strategy.e.Proof p0)");
    }

}