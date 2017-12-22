package com.artemis.generator.generator;

import com.artemis.ArtemisPlugin;
import com.artemis.Component;
import com.artemis.generator.common.SourceGenerator;
import com.artemis.generator.model.type.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Daan van Yperen
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
                Assert.assertEquals("test", cu.getTypes().get(0).getNameAsString());
            }
        } );
    }

    @Test
    public void When_agnostic_model_has_parameterized_method_Should_generate_valid_java_method_with_parameters() {
        TypeModel model = new TypeModel();
        MethodDescriptor method = new MethodDescriptor(void.class, "pos");
        method.addParameter(new ParameterDescriptor(int.class, "a"));
        model.add(method);

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("int a"));
            }
        } );
    }

    @Test
    public void When_agnostic_model_specifies_method_accesslevel_Should_generate_access_level_on_java_method() {
        TypeModel model = new TypeModel();
        MethodDescriptor method = new MethodDescriptor(void.class, "pos");
        method.setAccessLevel(AccessLevel.PROTECTED);
        model.add(method);

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("protected void pos"));
            }
        } );
    }

    @Test
    public void When_agnostic_model_specifies_method_accesslevel_Should_generate_access_level_on_java_field() {
        TypeModel model = new TypeModel();
        FieldDescriptor field = new FieldDescriptor(int.class, "pos");
        field.setAccessLevel(AccessLevel.PROTECTED);
        model.add(field);

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("protected int pos"));
            }
        } );
    }

    @Test
    public void When_specifying_superclass_Should_generate_valid_java_class_with_superclass() {
        TypeModel model = new TypeModel();
        model.superclass = Component.class;

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.toString().contains("extends Component"));
            }
        } );
    }

    @Test
    public void When_specifying_interface_Should_generate_valid_java_class_with_superinterface() {
        TypeModel model = new TypeModel();
        model.superinterface = ArtemisPlugin.class;

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.toString().contains("implements ArtemisPlugin"));
            }
        } );
    }

    @Test
    public void When_agnostic_model_has_method_Should_generate_valid_java_method() {
        TypeModel model = new TypeModel();
        model.add(new MethodDescriptor(void.class,"pos"));

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                assertHasMethod(cu, "void", "pos", 0);
            }
        } );
    }

    @Test
    public void When_agnostic_model_has_static_method_Should_generate_valid_static_java_method() {
        TypeModel model = new TypeModel();
        MethodDescriptor method = new MethodDescriptor(void.class, "pos");
        method.setStatic(true);
        model.add(method);

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("static"));
            }
        } );
    }

    @Test
    public void When_agnostic_model_has_field_Should_generate_valid_java_field() {
        TypeModel model = new TypeModel();
        model.add(new FieldDescriptor(int.class,"a"));

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("int a"));
            }
        } );
    }


    @Test
    public void When_agnostic_model_has_field_with_parameterized_type_Should_generate_valid_parameterized_type_java_field() {
        class _T {}
        class _T2 {}
        TypeModel model = new TypeModel();
        model.add(new FieldDescriptor(new ParameterizedTypeImpl(_T.class,_T2.class),"a"));

        generate(model, new ParseTest() {
            @Override
            public void test(CompilationUnit cu) {
                Assert.assertTrue(cu.getTypes().get(0).getMembers().toString().contains("T2>"));
            }
        } );
    }

    @Test
    public void When_method_has_statements_Should_generate_valid_java_method_with_statements() {
        TypeModel model = new TypeModel();
        MethodDescriptor method = new MethodDescriptor(void.class, "pos");
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
        } catch (ParseProblemException e) {
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

    private class HasMethodAsserter extends VoidVisitorAdapter<Void> {

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
        public void visit(MethodDeclaration n, Void arg) {
            if ( n.getNameAsString().equals(methodName) && n.getType().asString().equals(returnType) && n.getParameters().size()== parameterCount) matchingMethods++;
        }
    }
}
