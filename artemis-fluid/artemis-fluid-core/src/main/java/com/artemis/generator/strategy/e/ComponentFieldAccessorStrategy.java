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
 * Adds methods to interact with component fields.
 *
 * {@code class Pos extends Component { int x, y };} would extend the fluid interface with things
 * like {@code e.posX(50).posY(20); float x = e.posX(); }
 *
 * @author Daan van Yperen
 */
public class ComponentFieldAccessorStrategy extends IterativeModelStrategy {

    @Override
    protected void apply(ComponentDescriptor component, TypeModel model) {
        final Set<Field> fields = component.getAllPublicFields();
        exposeFields(component, model, fields);
    }

    private void exposeFields(ComponentDescriptor component, TypeModel model, Set<Field> fields) {
        for (Field field : fields) {
            exposeOnFluidInterface(component, field, model);
        }
    }

    private void exposeOnFluidInterface(ComponentDescriptor component, Field field, TypeModel model) {

        // do not expose setters on final fields.
        if (0 == (Modifier.FINAL & field.getModifiers())) {
            model.add(fieldSetterMethod(component, field));
        }

	    model.add(fieldGetterMethod(component, field));
    }

    /**
     * int E::posX() -> obtain field directly via interface.
     */
    private MethodDescriptor fieldGetterMethod(ComponentDescriptor component, Field field) {
        return new MethodBuilder(field.getGenericType(), component.getCompositeName(field.getName()))
                .mapper("return ", component, ".create(entityId)." + field.getName())
                .debugNotes(field.toGenericString())
                .build();
    }

    /**
     * E E::posX(10) -> set field, returns fluid interface.
     */
    private MethodDescriptor fieldSetterMethod(ComponentDescriptor component, Field field) {
        final String parameterName = field.getName();
        return new MethodBuilder(FluidTypes.E_TYPE, component.getCompositeName(parameterName))
                .parameter(field.getGenericType(), parameterName)
                .mapper(component, ".create(this.entityId)." + parameterName + "=" + parameterName)
                .debugNotes(field.toGenericString())
                .returnFluid()
                .build();
    }


}
