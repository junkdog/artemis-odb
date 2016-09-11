package com.artemis.generator.generator;

import com.artemis.BaseSystem;
import com.artemis.Main;
import com.artemis.generator.common.SourceGenerator;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.ParameterDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Convert agnostic class model to java source using Javapoet.
 *
 * Not a farting sound generator. Different kind of poet!
 *
 * <p>
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

        if ( model.superclass != null )
        {
            builder.superclass(model.superclass);
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
                        field.type, field.name);

        if ( field.isStatic() ) builder.addModifiers(Modifier.STATIC);

        switch (field.getAccessLevel())
        {
            case PROTECTED: builder.addModifiers(Modifier.PROTECTED); break;
            case PRIVATE: builder.addModifiers(Modifier.PRIVATE); break;
            case PUBLIC: builder.addModifiers(Modifier.PUBLIC); break;
            case UNSPECIFIED: break;
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
                .returns(method.returnType);

        if ( method.isStatic() ) builder.addModifiers(Modifier.STATIC);

        switch (method.getAccessLevel())
        {
            case PROTECTED: builder.addModifiers(Modifier.PROTECTED); break;
            case PRIVATE: builder.addModifiers(Modifier.PRIVATE); break;
            case PUBLIC: builder.addModifiers(Modifier.PUBLIC); break;
            case UNSPECIFIED: break;
        }

        for (ParameterDescriptor parameter : method.parameters) {
            builder.addParameter(generateParameterSpecs(parameter));
        }


        for (String statement : method.statements) {
            builder.addStatement(statement);
        }

        return builder.build();
    }

    private ParameterSpec generateParameterSpecs(ParameterDescriptor parameter) {
        return ParameterSpec.builder(parameter.type, parameter.name).build();
    }


}
