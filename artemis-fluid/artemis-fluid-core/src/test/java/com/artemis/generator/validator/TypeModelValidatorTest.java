package com.artemis.generator.validator;

import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.Log;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class TypeModelValidatorTest {

    private class NullLog implements Log {

        @Override
        public void info(String msg) {
        }

        @Override
        public void error(String msg) {
        }
    };

    @Test
    public void When_unique_method_Should_succeed() {
        final TypeModel model = new TypeModel();
        model.add(new MethodDescriptor(void.class,"test"));
        validate(model);
    }

    @Test
    public void When_unique_field_Should_succeed() {
        final TypeModel model = new TypeModel();
        model.add(new FieldDescriptor(int.class,"test"));
        validate(model);
    }

    @Test(expected = TypeModelValidatorException.class)
    public void When_ambiguous_field_Should_fail() {
        final TypeModel model = new TypeModel();
        model.add(new FieldDescriptor(int.class,"test"));
        model.add(new FieldDescriptor(long.class,"test"));
        validate(model);
    }

    @Test(expected = TypeModelValidatorException.class)
    public void When_ambiguous_method_Should_fail() {
        final TypeModel model = new TypeModel();
        model.add(new MethodDescriptor(void.class,"test"));
        model.add(new MethodDescriptor(int.class,"test"));
        validate(model);
    }

    @Test
    public void When_overloaded_method_Should_succeed() {
        final TypeModel model = new TypeModel();

        MethodDescriptor method = new MethodDescriptor(void.class, "test");
        method.addParameter(new ParameterDescriptor(int.class, "v1"));
        model.add(method);

        MethodDescriptor method2 = new MethodDescriptor(void.class, "test");
        method2.addParameter(new ParameterDescriptor(long.class, "v2"));
        model.add(method2);

        validate(model);
    }

    @Test(expected = TypeModelValidatorException.class)
    public void When_ambiguous_overloaded_method_Should_fail() {
        final TypeModel model = new TypeModel();

        MethodDescriptor method = new MethodDescriptor(void.class, "test");
        method.addParameter(new ParameterDescriptor(int.class, "v1"));
        model.add(method);

        MethodDescriptor method2 = new MethodDescriptor(void.class, "test");
        method2.addParameter(new ParameterDescriptor(int.class, "v2"));
        model.add(method2);

        validate(model);
    }

    private void validate(TypeModel model) {
        new TypeModelValidator(new NullLog(),"test").validate(model);
    }

}