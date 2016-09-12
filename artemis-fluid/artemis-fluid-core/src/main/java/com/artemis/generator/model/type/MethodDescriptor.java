package com.artemis.generator.model.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes method.
 *
 * @author Daan van Yperen
 */
public class MethodDescriptor {

    public String name;
    /** optional */
    public Type returnType;
    public List<String> statements = new ArrayList<String>();
    public List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();
    private boolean isStatic;
    private AccessLevel accessLevel = AccessLevel.PUBLIC;
    private String debugNotes;


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
        return signature(true);
    }

    public String signature(boolean variableNames) {
        if ( returnType instanceof Class) return ((Class)returnType).getCanonicalName() + " " + name + "()";
        return returnType.toString() + " " + name + "("+parameterSignature(variableNames)+")";
    }

    private String parameterSignature(boolean variableNames) {
        String s = "";
        for (ParameterDescriptor parameter : parameters) {
            s = s + ( !s.isEmpty() ? "," : "" ) + parameter.signature(variableNames);
        }
        return s;
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

    public void setDebugNotes(String debugNotes) {
        this.debugNotes = debugNotes;
    }

    public String getDebugNotes() {
        return debugNotes;
    }
}
