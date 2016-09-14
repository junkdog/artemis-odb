package com.artemis.generator.model.artemis;

import com.artemis.Component;
import com.artemis.annotations.Fluid;
import org.junit.Assert;
import org.junit.Test;

/**
 * Fluid annotation tests.
 *
 * @author Daan van Yperen
 */
public class ComponentDescriptorTest {

    @Fluid(name = "oneTwo")
    private class ValidNamed extends Component {}

    @Fluid()
    private class EmptyNamed extends Component {}

    @Test
    public void When_component_has_type_annotation_ComponentDescriptor_Should_use_specified_name()
    {
        ComponentDescriptor descriptor = ComponentDescriptor.create(ValidNamed.class);
        Assert.assertEquals("OneTwo", descriptor.getName());
        Assert.assertEquals("oneTwo", descriptor.getMethodPrefix());
    }

    @Test
    public void When_component_has_empty_type_name_annotation_ComponentDescriptor_Should_ignore_name()
    {
        ComponentDescriptor descriptor = ComponentDescriptor.create(EmptyNamed.class);
        Assert.assertEquals("EmptyNamed", descriptor.getName());
        Assert.assertEquals("emptyNamed", descriptor.getMethodPrefix());
    }

}
