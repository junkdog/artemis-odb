package com.artemis.generator.strategy.e;

import static com.artemis.generator.util.ExtendedTypeReflection.resolveGenericType;
import static com.artemis.generator.util.ExtendedTypeReflection.resolveGenericReturnType;
import com.artemis.annotations.Fluid;
import com.artemis.annotations.FluidMethod;
import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Adds methods that proxy component methods.
 * <p>
 * Exposes all public methods, using some naming conventions. Generally methods called {@ode get} and {@code set} are
 * collapsed into the component name for convenience. {@link ComponentDescriptor#getCompositeName(String)}.
 * <p>
 * In case of methods that have both parameters and a return value, user preference defines if the fluid interface
 * or the parameter will be returned.
 *
 * @author Daan van Yperen
 */
public class ComponentMethodProxyStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        final Set<Method> methods = component.getAllPublicMethods();
        exposeMethods(component, model, methods);
    }

    private void exposeMethods(ComponentDescriptor component, TypeModel model, Set<Method> methods) {
        for (Method method : methods) {
            exposeOnFluidInterface(component, method, model);
        }
    }

    private void exposeOnFluidInterface(ComponentDescriptor component, Method method, TypeModel model) {

        FluidMethod methodAnnotation = method.getAnnotation(FluidMethod.class);
        if (methodAnnotation != null && methodAnnotation.exclude()) return;

        if (isSetter(method)) {
            model.add(methodProxyReturnFluidMethod(component, method));
        } else if (component.getPreferences().swallowGettersWithParameters && isGetterWithParameters(method)) {
            // by preference, call getters and swallow the returned value, instead returning the fluid interface.
            model.add(methodProxyReturnFluidMethod(component, method));
        } else {
            // return the fluid interface.
            model.add(methodProxyMethod(component, method));
        }
    }

    private boolean isGetterWithParameters(Method method) {
        return !isSetter(method) && method.getParameterTypes().length > 0;
    }

    private boolean isSetter(Method method) {
        return void.class.equals(method.getReturnType());
    }

    private MethodDescriptor methodProxyReturnFluidMethod(ComponentDescriptor component, Method method) {
        MethodBuilder builder = new MethodBuilder(FluidTypes.E_TYPE, component.getCompositeName(method.getName()));

        String arguments = appendParameters(component, method, builder);

        return builder
                .mapper(component, ".create(entityId)." + method.getName() + "(" + arguments + ")")
                .debugNotes(method.toGenericString())
                .returnFluid()
                .build();
    }

    private String appendParameters(ComponentDescriptor component, Method method, MethodBuilder builder) {
        String arguments = "";
        final Type[] parameterTypes = method.getGenericParameterTypes();
        final int paramCount = parameterTypes.length;
        for(int i = 0; i < paramCount; i++) {
            builder.parameter(resolveGenericType(component, method, parameterTypes[i]), "p" + i);
            arguments = arguments + (arguments.isEmpty() ? "" : ", ") + "p" + i;
        }
        return arguments;
    }

    private MethodDescriptor methodProxyMethod(ComponentDescriptor component, Method method) {

        MethodBuilder builder = new MethodBuilder(resolveGenericReturnType(component, method), component.getCompositeName(method.getName()));

        String arguments = appendParameters(component, method, builder);

        return builder
                .mapper("return ", component, ".create(entityId)." + method.getName() + "(" + arguments + ")")
                .debugNotes(method.toGenericString())
                .build();
    }
}
