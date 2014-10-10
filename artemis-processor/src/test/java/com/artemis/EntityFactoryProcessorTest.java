package com.artemis;

import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import javax.tools.JavaFileObject;

import org.junit.Test;

@SuppressWarnings("static-method")
public class EntityFactoryProcessorTest {
	
	@Test
	public void test_entity_factory_sanity() {
		ASSERT.about(javaSource())
			.that(source("ExhibitA"))
			.processedWith(new EntityFactoryProcessor())
			.compilesWithoutError();
	}
	
	@Test
	public void test_extended_entity_factory_sanity() {
		ASSERT.about(javaSource())
			.that(source("Extended"))
			.processedWith(new EntityFactoryProcessor())
			.compilesWithoutError();
	}
	
	@Test
	public void wrong_field_error() {
		ASSERT.about(javaSource())
			.that(source("WrongParamName"))
			.processedWith(new EntityFactoryProcessor())
			.failsToCompile()
			.withErrorContaining("has no field named");
	}
	
	@Test
	public void wrong_generic_type_declaration() {
		ASSERT.about(javaSource())
			.that(source("WrongExtends"))
			.processedWith(new EntityFactoryProcessor())
			.failsToCompile()
			.withErrorContaining("Expected EntityFactory<WrongExtends>, but found");
	}
	
	@Test
	public void too_many_components_on_method() {
		ASSERT.about(javaSource())
			.that(source("TooManyRefs"))
			.processedWith(new EntityFactoryProcessor())
			.failsToCompile()
			.withErrorContaining("@Bind on methods limited to one component type");
	}
	
	@Test
	public void no_component_to_automatch() {
		ASSERT.about(javaSource())
			.that(source("NoAutoMatch"))
			.processedWith(new EntityFactoryProcessor())
			.failsToCompile()
			.withErrorContaining("Unable to match component for position");
	}
	
	private static JavaFileObject source(String fileName) {
		return forResource("com/artemis/factory/" + fileName + ".java");
	}
}
