package com.artemis.generator.model.artemis;

import com.artemis.Component;
import com.artemis.generator.util.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withModifier;

/**
 * Describes an artemis component.
 *
 * @author Daan van Yperen
 */
public class ComponentDescriptor {
    public Class<? extends Component> type;

    private Set<Field> allPublicFields;
    private Set<Method> allPublicMethods;

    public ComponentDescriptor(Class<? extends Component> type) {
        this.type = type;
    }

    /** simple class name. 'RocketFuel' */
    public String getName() { return type.getSimpleName(); };

    /** decapitalized class name. 'rocketFuel' */
    public String getMethodPrefix() { return Strings.decapitalizeString(type.getSimpleName()); };

    public Class getComponentType() {
        return type;
    }

    public String getCompositeName(String suffix) {
        if ( suffix.startsWith("get")
                || suffix.startsWith("set") ) return
                suffix.length() <= 3 ? getMethodPrefix() : getCompositeName(suffix.substring(3));
        return getMethodPrefix() + Strings.capitalizeString(suffix);
    }

    /**
     * Get if component is a flag component.
     * @return {@code true} is simple flag, {@code false} if it is a data container.
     */
    public boolean isFlagComponent()
    {
         return getAllPublicFields().isEmpty() &&
                     getAllPublicMethods().isEmpty();
    }


    /** Get all public fields on this component, cached. */
    @SuppressWarnings("unchecked")
    public Set<Field> getAllPublicFields() {
        if ( allPublicFields == null )
        {
            allPublicFields = getAllFields(type, withModifier(Modifier.PUBLIC));
        }
        return allPublicFields;
    }

    /** Get all public methods on this component, cached. */
    @SuppressWarnings("unchecked")
    public Set<Method> getAllPublicMethods() {
        if ( allPublicMethods == null ) {
            allPublicMethods = getAllMethods(type, withModifier(Modifier.PUBLIC));
        }
        return allPublicMethods;
    }
}
