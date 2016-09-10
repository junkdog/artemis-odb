package com.artemis.generator.model.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daan on 10-9-2016.
 */
public class MethodDescriptor {

    public String name;
    public Class returnType;
    public List<String> statements = new ArrayList<String>();
    public List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();


    public MethodDescriptor(Class returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }

    public void addStatement(String statement) {
        statements.add(statement);
    }

    @Override
    public String toString() {
        return signature();
    }

    public String signature() {
        return returnType.getCanonicalName() + " " + name + "()";
    }

    public void addParameter(ParameterDescriptor parameter) {
        parameters.add(parameter);
    }
}
