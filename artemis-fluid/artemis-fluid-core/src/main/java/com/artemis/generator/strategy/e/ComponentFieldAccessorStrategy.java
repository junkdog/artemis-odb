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
        Class type = component.getComponentType();

        Set<Field> fields = getAllFields(type, withModifier(Modifier.PUBLIC));
        exposeFields(component, model, fields);

        Set<Method> methods = getAllMethods(type, withModifier(Modifier.PUBLIC));
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

        if (void.class.equals(method.getReturnType()))
        {
            model.add(methodSetterMethod(component, method));
        } else{
            model.add(
                    methodGetterMethod(component, method));
        }
    }


    private void exposeOnFluidInterface(ComponentDescriptor component, Field field, TypeModel model) {
        model.add(fieldSetterMethod(component, field));
        model.add(fieldGetterMethod(component, field));
    }


    private MethodDescriptor methodSetterMethod(ComponentDescriptor component, Method method) {
        MethodBuilder builder = new MethodBuilder(FluidTypes.E_TYPE, component.getCompositeName(method.getName()));

        int count = 0;
        String arguments = "";
        for (Class<?> parameterType : method.getParameterTypes()) {
            builder.parameter(parameterType, "p" + count);
            arguments = arguments + (arguments.isEmpty() ? "" : ", ") + "p" + count;
            count++;
        }

        return builder
                .mapper(component, ".get(entityId)." + method.getName() + "(" + arguments + ")")
                .debugNotes(method.toGenericString())
                .returnFluid()
                .build();
    }

    private MethodDescriptor methodGetterMethod(ComponentDescriptor component, Method method) {
        return new MethodBuilder(method.getGenericReturnType(), component.getCompositeName(method.getName()))
                .mapper("return ", component, ".get(entityId)." + method.getName() +"()")
                .debugNotes(method.toGenericString())
                .build();
    }

    /**
     * T componentName() -> create new entity.
     */
    private MethodDescriptor fieldGetterMethod(ComponentDescriptor component, Field field) {
        return new MethodBuilder(field.getGenericType(), component.getCompositeName(field.getName()))
                .mapper("return ", component, ".get(entityId)." + field.getName())
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
                .mapper(component, ".get(entityId)." + parameterName + "=" + parameterName)
                .debugNotes(field.toGenericString())
                .returnFluid()
                .build();
    }


}
