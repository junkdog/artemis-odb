package com.artemis.generator.strategy.e;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;
import com.google.common.base.Preconditions;

import java.lang.reflect.*;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;

/**
 * @author Daan van Yperen
 */
public class ComponentFieldAccessorStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        final Set<Field> fields = component.getAllPublicFields();
        exposeFields(component, model, fields);

        final Set<Method> methods = component.getAllPublicMethods();
        exposeMethods(component, model, methods);
    }

    private void exposeMethods(ComponentDescriptor component, TypeModel model, Set<Method> methods) {
        for (Method method : methods) {
            exposeOnFluidInterface(component, method, model);
        }
    }

    private void exposeFields(ComponentDescriptor component, TypeModel model, Set<Field> fields) {
        for (Field field : fields) {
            exposeOnFluidInterface(component, field, model);
        }
    }

    private void exposeOnFluidInterface(ComponentDescriptor component, Method method, TypeModel model) {

        if (isSetter(method)) {
            model.add(methodSetterMethod(component, method));
        } else if (component.getPreferences().swallowGettersWithParameters && isGetterWithParameters(method)) {
            // by preference, call getters and swallow the returned value, instead returning the fluid interface.
            model.add(methodSetterMethod(component, method));
        } else
            // return the return type.
            model.add(methodGetterMethod(component, method));
    }

    private boolean isGetterWithParameters(Method method) {
        return !isSetter(method) && method.getParameterTypes().length > 0;
    }

    private boolean isSetter(Method method) {
        return void.class.equals(method.getReturnType());
    }


    private void exposeOnFluidInterface(ComponentDescriptor component, Field field, TypeModel model) {
        if (0 == (Modifier.FINAL & field.getModifiers()))
	        model.add(fieldSetterMethod(component, field));

	    model.add(fieldGetterMethod(component, field));
    }


    private MethodDescriptor methodSetterMethod(ComponentDescriptor component, Method method) {
        MethodBuilder builder = new MethodBuilder(FluidTypes.E_TYPE, component.getCompositeName(method.getName()));

        String arguments = appendParameters(method, builder);

        return builder
                .mapper(component, ".create(entityId)." + method.getName() + "(" + arguments + ")")
                .debugNotes(method.toGenericString())
                .returnFluid()
                .build();
    }

    private String appendParameters(Method method, MethodBuilder builder) {
        int count = 0;
        String arguments = "";
        for (Class<?> parameterType : method.getParameterTypes()) {
            builder.parameter(parameterType, "p" + count);
            arguments = arguments + (arguments.isEmpty() ? "" : ", ") + "p" + count;
            count++;
        }
        return arguments;
    }

    private MethodDescriptor methodGetterMethod(ComponentDescriptor component, Method method) {

        MethodBuilder builder = new MethodBuilder(method.getGenericReturnType(), component.getCompositeName(method.getName()));

        String arguments = appendParameters(method, builder);

        return builder
                .mapper("return ", component, ".create(entityId)." + method.getName() + "(" + arguments + ")")
                .debugNotes(method.toGenericString())
                .build();
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor fieldGetterMethod(ComponentDescriptor component, Field field) {
        return new MethodBuilder(field.getGenericType(), component.getCompositeName(field.getName()))
                .mapper("return ", component, ".create(entityId)." + field.getName())
                .debugNotes(field.toGenericString())
                .build();
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor fieldSetterMethod(ComponentDescriptor component, Field field) {
        final String parameterName = field.getName();
        return new MethodBuilder(FluidTypes.E_TYPE, component.getCompositeName(parameterName))
                .parameter(field.getGenericType(), parameterName)
                .mapper(component, ".create(entityId)." + parameterName + "=" + parameterName)
                .debugNotes(field.toGenericString())
                .returnFluid()
                .build();
    }


}
