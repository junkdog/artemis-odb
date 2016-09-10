package com.artemis.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Sourcecode generator agnostic model of class.
 * <p>
 * Created by Daan on 10-9-2016.
 */
public class ClassModel {
    public List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();

    /** Add method to model. */
    public void add(MethodDescriptor method) {
        methods.add(method);
    }

    /**
     * Get method that matches signature exactly.
     * @return {@code method}, or {@code null}.
     */
    public MethodDescriptor getMethodBySignature(String signature) {
        for (MethodDescriptor method : methods) {
            if (signature.equals(method.signature())) {
                return method;
            }
        }
        return null;
    }

}
