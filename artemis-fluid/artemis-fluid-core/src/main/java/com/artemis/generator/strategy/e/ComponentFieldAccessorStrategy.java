package com.artemis.generator.strategy.e;

import com.artemis.generator.common.IterativeModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Set;

/**
 * Adds methods to interact with component fields.
 * @see DefaultFieldProxyStrategy for default getter/setter logic.
 * @author Daan van Yperen
 */
public class ComponentFieldAccessorStrategy extends IterativeModelStrategy {

    private Collection<FieldProxyStrategy> fieldProxyStrategies;

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        fieldProxyStrategies = artemisModel.fieldProxyStrategies;
        super.apply(artemisModel, model);
    }

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        final Set<Field> fields = component.getAllPublicFields();
        exposeFields(component, model, fields);
    }

    private void exposeFields(ComponentDescriptor component, TypeModel model, Set<Field> fields) {
        for (Field field : fields) {
            for (FieldProxyStrategy fieldProxyStrategy : fieldProxyStrategies) {
                if (fieldProxyStrategy.matches(component, field, model)) {
                    fieldProxyStrategy.execute(component, field, model);
                    break;
                }
            }

        }
    }

}
