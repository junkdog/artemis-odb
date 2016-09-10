package com.artemis.generator.strategy;

import com.artemis.Component;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.ClassModel;
import com.artemis.generator.model.ComponentDescriptor;
import com.artemis.generator.model.MethodDescriptor;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to run strategies.
 *
 * Created by Daan on 10-9-2016.
 */
public abstract class StrategyTest {

    /** Run strategy on given components. */
    protected ClassModel applyStrategy(Class<? extends BuilderModelStrategy> strategyClazz, Class<? extends Component>... components) {
        final ClassModel model = new ClassModel();
        try {
            (strategyClazz.newInstance()).apply(asDescriptorList(components), model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return model;
    }

    /** Convert component list to componentdescriptor list. */
    protected Collection<ComponentDescriptor> asDescriptorList(Class<? extends Component>[] components) {
        final ArrayList<ComponentDescriptor> results = new ArrayList<ComponentDescriptor>();
        for (Class<? extends Component> component : components) {
            results.add(new ComponentDescriptor(component));
        }
        return results;
    }

    /** Assert if model matches signature. If not found, display all known signatures. */
    public void assertHasMethod(ClassModel model, String signature) {
        Assert.assertNotNull("Expected '" +signature+"' but not found.\n\rMethods:\n\r"+methodCollation(model.methods), model.getMethodBySignature(signature));
    }

    private String methodCollation(List<MethodDescriptor> methods) {
        String s ="";
        for (MethodDescriptor method : methods) {
            s += method.signature() +"\n\r";
        }
        return s;
    }


}
