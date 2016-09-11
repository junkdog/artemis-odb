package com.artemis.generator.model.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class MethodDescriptor {

    public String name;
    public Type returnType;
    public List<String> statements = new ArrayList<String>();
    public List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();
    private boolean isStatic;
    private AccessLevel accessLevel = AccessLevel.PUBLIC;


    public MethodDescriptor(Type returnType, String name) {
        this.returnType = returnType
        ;
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
        if ( returnType instanceof Class) return ((Class)returnType).getCanonicalName() + " " + name + "()";
        return returnType.toString() + " " + name + "()";
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
