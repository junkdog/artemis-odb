package com.artemis.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daan on 10-9-2016.
 */
public class MethodDescriptor {

    public String name;
    public String returnType;
    public List<String> statements = new ArrayList<String>();


    public MethodDescriptor(String returnType, String name) {
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
        return returnType + " " + name + "()";
    }
}
