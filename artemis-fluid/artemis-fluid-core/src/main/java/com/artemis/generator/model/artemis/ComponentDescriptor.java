package com.artemis.generator.model.artemis;

import com.artemis.Component;
import com.artemis.FluidGeneratorPreferences;
import com.artemis.annotations.Fluid;
import com.artemis.generator.util.ExtendedTypeReflection;
import com.artemis.generator.util.Strings;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

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
    public final Class<? extends Component> type;
    private final String methodPrefix;
    private final String name;
    private final FluidGeneratorPreferences preferences;

    private ComponentDescriptor(Class<? extends Component> type, String methodPrefix, String name, FluidGeneratorPreferences preferences) {
        this.type = type;
        this.methodPrefix = methodPrefix;
        this.name = name;
        this.preferences = preferences;
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

    public FluidGeneratorPreferences getPreferences() {
        return preferences;
    }

    /** Create descriptor for passed type. */
    public static ComponentDescriptor create(Class<? extends Component> type) {

        String methodPrefix = Strings.decapitalizeString(type.getSimpleName());
        String name = type.getSimpleName();
        FluidGeneratorPreferences preferences = new FluidGeneratorPreferences();

        // @todo make sure this is processed from least to most pressing.
        for (Annotation annotation : ExtendedTypeReflection.getAllAnnotations(type)) {
            if (annotation instanceof Fluid) {
                final Fluid fluid = (Fluid) annotation;
                if (!fluid.name().isEmpty()) {
                    methodPrefix = fluid.name();
                    name = Strings.capitalizeString(fluid.name());
                }
                preferences.apply(fluid);
            }
        }

        return new ComponentDescriptor(type, methodPrefix, name, preferences );
    }
}
