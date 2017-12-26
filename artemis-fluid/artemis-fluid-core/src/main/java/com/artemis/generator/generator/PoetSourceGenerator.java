package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;
import com.artemis.generator.model.type.*;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Convert agnostic class model to java source using Javapoet.
 * <p>
 * Not a farting sound generator. Different kind of poet!
 * Dutch joke please ignore.
 * <p>
 *
 * @author Daan van Yperen
 */
public class PoetSourceGenerator implements SourceGenerator {

    @Override
    public void generate(TypeModel model, Appendable out) {
        try {
            JavaFile javaFile
                    = JavaFile.builder(model.packageName, generateTypeSpec(model)).build();
            javaFile.writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private TypeSpec generateTypeSpec(TypeModel model) {
        TypeSpec.Builder builder = TypeSpec
                .classBuilder(model.name)
                .addFields(generateFieldSpecs(model))
                .addMethods(generateMethodSpecs(model))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        if (model.superclass != null) {
            builder.superclass(model.superclass);
        }

        if (model.superinterface != null) {
            builder.addSuperinterface(model.superinterface);
        }

        return builder.build();
    }

    private Iterable<FieldSpec> generateFieldSpecs(TypeModel model) {
        ArrayList<FieldSpec> results = new ArrayList<FieldSpec>(16);
        for (FieldDescriptor field : model.fields) {
            results.add(generateFieldSpec(field));
        }
        return results;
    }

    private FieldSpec generateFieldSpec(FieldDescriptor field) {
        FieldSpec.Builder builder =
                FieldSpec.builder(
                        getTypeName(field.type), field.name);

        if (field.isFinal()) builder.addModifiers(Modifier.FINAL);
        if (field.isStatic()) builder.addModifiers(Modifier.STATIC);

        if (field.getInitializer() != null) {
            builder.initializer(field.getInitializer());
        }

        switch (field.getAccessLevel()) {
            case PROTECTED:
                builder.addModifiers(Modifier.PROTECTED);
                break;
            case PRIVATE:
                builder.addModifiers(Modifier.PRIVATE);
                break;
            case PUBLIC:
                builder.addModifiers(Modifier.PUBLIC);
                break;
            case UNSPECIFIED:
                break;
        }

        return builder.build();
    }

    private Iterable<MethodSpec> generateMethodSpecs(TypeModel model) {
        ArrayList<MethodSpec> results = new ArrayList<MethodSpec>(16);
        for (MethodDescriptor method : model.methods) {
            results.add(generateMethodSpec(method));
        }

        return results;
    }

    private MethodSpec generateMethodSpec(MethodDescriptor method) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.name)
                .returns(getTypeName(method.returnType));

        if (method.isStatic()) builder.addModifiers(Modifier.STATIC);
        if (method.isVarargs()) builder.varargs(true);

        switch (method.getAccessLevel()) {
            case PROTECTED:
                builder.addModifiers(Modifier.PROTECTED);
                break;
            case PRIVATE:
                builder.addModifiers(Modifier.PRIVATE);
                break;
            case PUBLIC:
                builder.addModifiers(Modifier.PUBLIC);
                break;
            case UNSPECIFIED:
                break;
        }

        if ( method.getJavadoc() != null ) {
            builder.addJavadoc(method.getJavadoc());
        }

        for (ParameterDescriptor parameter : method.parameters) {
            builder.addParameter(generateParameterSpecs(parameter));
        }


        for (String statement : method.statements) {
            builder.addStatement(statement);
        }

        return builder.build();
    }

    private TypeName getTypeName(Type type) {
        if (type instanceof TypeDescriptor) {
            return ClassName.bestGuess(type.toString());
        }
        return TypeName.get(type);
    }

    private ParameterSpec generateParameterSpecs(ParameterDescriptor parameter) {
        return ParameterSpec.builder(getTypeName(parameter.type), parameter.name).build();
    }


}
