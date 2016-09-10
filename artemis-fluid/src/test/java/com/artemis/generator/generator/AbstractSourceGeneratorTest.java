package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Daan on 10-9-2016.
 */
public abstract class AbstractSourceGeneratorTest {

    @Test
    public void When_agnostic_model_empty_Should_Generate_valid_java_class() {
        TypeModel model = new TypeModel();
        model.name = "test";

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                assertParses(cu);
                Assert.assertEquals("test", cu.getTypes().get(0).getName());
            }
        } );
    }

    @Test
    public void When_agnostic_model_has_method_Should_generate_valid_java_method() {
        TypeModel model = new TypeModel();
        model.add(new MethodDescriptor("void","pos"));

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                assertHasMethod(cu, "void", "pos", 0);
            }
        } );
    }

    @Test
    public void When_method_has_statements_Should_generate_valid_java_method_with_statements() {
        TypeModel model = new TypeModel();
        MethodDescriptor method = new MethodDescriptor("void", "pos");
        method.addStatement("int a=0;");
        model.add(method);

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().get(0).toString().contains("int a"));
            }
        } );
    }

    private void generate(TypeModel model, ParseTest parseTest) {
        parseTest.test(getCompilationUnit(getBytes(model)));
    }

    private CompilationUnit getCompilationUnit(byte[] bytes) {
        try {
            return JavaParser.parse(new ByteArrayInputStream(bytes));
        } catch (ParseException e) {
            return null;
        }
    }

    private byte[] getBytes(TypeModel model) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(outputStream);
        getSourceGenerator().generate(model, stream);
        return outputStream.toByteArray();
    }

    protected abstract SourceGenerator getSourceGenerator();


    public static abstract class ParseTest {
        public abstract void test( CompilationUnit cu );

        public void assertParses( CompilationUnit cu ) {
            Assert.assertNotNull("java not parseable.", cu);
        }
    }

    private void assertHasMethod(CompilationUnit cu, String returnType, String methodName, int parameterCount) {
        final HasMethodAsserter asserter = new HasMethodAsserter(returnType, methodName, parameterCount);
        asserter.visit(cu, null);
        Assert.assertTrue("No matching methods found.",  asserter.matchingMethods==1);
    }

    private class HasMethodAsserter extends VoidVisitorAdapter {

        private final int parameterCount;
        private final String methodName;
        private final String returnType;
        public int matchingMethods =0;

        public HasMethodAsserter(String returnType, String methodName, int parameterCount) {
            this.parameterCount = parameterCount;
            this.methodName = methodName;
            this.returnType = returnType;
        }

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            if ( n.getName().equals(methodName) && n.getType().toString().equals(returnType) && n.getParameters().size()== parameterCount) matchingMethods++;
        }
    }
}
