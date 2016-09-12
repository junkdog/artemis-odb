package com.artemis.generator.model.artemis;

import com.artemis.Component;
import com.artemis.generator.util.Strings;

/**
 * Describes an artemis component.
 *
 * @author Daan van Yperen
 */
public class ComponentDescriptor {
    public Class<? extends Component> type;

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
}
