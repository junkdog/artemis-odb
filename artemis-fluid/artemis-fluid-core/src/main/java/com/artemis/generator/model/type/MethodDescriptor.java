package com.artemis.generator.model.type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class MethodDescriptor {

    public String name;
    public Class returnType;
    public List<String> statements = new ArrayList<String>();
    public List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();
    private boolean isStatic;
    private AccessLevel accessLevel = AccessLevel.PUBLIC;


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

    public void setStatic(boolean value) {
        this.isStatic = value;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
