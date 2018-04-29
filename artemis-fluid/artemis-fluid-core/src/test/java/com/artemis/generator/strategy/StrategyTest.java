package com.artemis.generator.strategy;

import com.artemis.Component;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.strategy.e.DefaultFieldProxyStrategy;
import com.artemis.generator.strategy.e.FieldProxyStrategy;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to run strategies.
 *
 * @author Daan van Yperen
 */
public abstract class StrategyTest {

    /**
     * Run strategy on given components.
     */
    protected TypeModel applyStrategy(Class<? extends BuilderModelStrategy> strategyClazz, Class<? extends Component>... components) {
        final TypeModel model = new TypeModel();
        try {
            final List<FieldProxyStrategy> fieldProxyStrategies = Collections.<FieldProxyStrategy>singletonList(new DefaultFieldProxyStrategy());
            (strategyClazz.newInstance()).apply(
                    new ArtemisModel(asDescriptorList(components), fieldProxyStrategies), model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return model;
    }

    /**
     * Convert component list to componentdescriptor list.
     */
    protected Collection<ComponentDescriptor> asDescriptorList(Class<? extends Component>[] components) {
        final ArrayList<ComponentDescriptor> results = new ArrayList<ComponentDescriptor>();
        for (Class<? extends Component> component : components) {
            results.add(ComponentDescriptor.create(component));
        }
        return results;
    }

    /**
     * Assert if model matches signature. If not found, display all known signatures.
     */
    public void assertHasMethod(TypeModel model, String signature) {
        Assert.assertNotNull("Expected '" + signature + "' but not found.\n\rMethods:\n\r" + methodCollation(model.methods), model.getMethodBySignature(signature));
    }

    /**
     * Assert if model matches signature. If not found, display all known signatures.
     */
    public void assertNoMethod(TypeModel model, String signature) {
        Assert.assertNull("Expected no '" + signature + "' but found.\n\rMethods:\n\r" + methodCollation(model.methods), model.getMethodBySignature(signature));
    }

    private String methodCollation(List<MethodDescriptor> methods) {
        String s = "";
        for (MethodDescriptor method : methods) {
            s += method.signature(true, true) + "\n\r";
        }
        return s;
    }


}
