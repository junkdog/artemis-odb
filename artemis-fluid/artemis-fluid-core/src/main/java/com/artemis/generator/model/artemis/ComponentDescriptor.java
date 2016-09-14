package com.artemis.generator.model.artemis;

import com.artemis.Component;
import com.artemis.annotations.Fluid;
import com.artemis.generator.util.ExtendedTypeReflection;
import com.artemis.generator.util.Strings;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Describes an artemis component.
 *
 * @author Daan van Yperen
 */
public class ComponentDescriptor {
    public Class<? extends Component> type;

    private String methodPrefix;
    private String name;

    private ComponentDescriptor(Class<? extends Component> type, String methodPrefix, String name) {
        this.type = type;
        this.methodPrefix = methodPrefix;
        this.name = name;
    }

    /**
     * simple class name. 'RocketFuel'
     */
    public String getName() {
        return name;
    }

    ;

    /**
     * decapitalized class name. 'rocketFuel'
     */
    public String getMethodPrefix() {

        return methodPrefix;
    }

    ;

    public Class getComponentType() {
        return type;
    }

    public String getCompositeName(String suffix) {
        if (suffix.startsWith("get")
                || suffix.startsWith("set")) return
                suffix.length() <= 3 ? getMethodPrefix() : getCompositeName(suffix.substring(3));
        return getMethodPrefix() + Strings.capitalizeString(suffix);
    }


    public Set<Field> getAllPublicFields() {
        return ExtendedTypeReflection.getAllPublicFields(type);
    }

    public Set<Method> getAllPublicMethods() {
        return ExtendedTypeReflection.getAllPublicMethods(type);
    }

    public boolean isFlagComponent() {
        return ExtendedTypeReflection.isFlagComponent(type);
    }

    /** Create descriptor for passed type. */
    public static ComponentDescriptor create(Class<? extends Component> type) {

        String methodPrefix = Strings.decapitalizeString(type.getSimpleName());
        String name = type.getSimpleName();

        for (Annotation annotation : ExtendedTypeReflection.getAllAnnotations(type)) {
            if (annotation instanceof Fluid) {
                final Fluid fluid = (Fluid) annotation;
                if (!fluid.name().isEmpty()) {
                    methodPrefix = fluid.name();
                    name = Strings.capitalizeString(fluid.name());
                }
            }
        }

        return new ComponentDescriptor(type, methodPrefix, name );
    }
}
