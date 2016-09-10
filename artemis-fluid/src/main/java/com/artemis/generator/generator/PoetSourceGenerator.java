package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Convert agnostic class model to java source using Javapoet.
 *
 * Not a farting sound generator. Different kind of poet!
 *
 * <p>
 * Created by Daan on 10-9-2016.
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
        return TypeSpec
                .classBuilder(model.name)
                .addMethods(generateMethodSpecs(model))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();
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
                .addModifiers(Modifier.PUBLIC)
                .returns(method.returnType);

        for (String statement : method.statements) {
            builder.addStatement(statement);
        }

        return builder.build();
    }


}
