package com.artemis;

import static com.artemis.FluentUtil.element;
import static com.artemis.FluentUtil.Match.ONE_OF;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("com.artemis.ComponentConformanceValidator")
public class ComponentValidatorProcessor extends AbstractProcessor {
	
	private static final String COMPONENT = "com.artemis.Component";
	private static final String PACKED_COMPONENT = "com.artemis.PackedComponent";
	private static final String POOLED_COMPONENT = "com.artemis.PooledComponent";
	
	@Override
	public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver() || types.size() == 0)
			return true;
		
		TypeElement annotation = types.iterator().next();
		for (TypeElement type: typesIn(roundEnv.getElementsAnnotatedWith(annotation))) {
			if (element(type).hasAnnotation("@com.artemis.annotations.PooledWeaver")) {
				ensureTypeExtendsComponent(type);
				pooledComponentCheck(type);
			} else if (element(type).hasAnnotation("@com.artemis.annotations.PackedWeaver")) {
				ensureTypeExtendsComponent(type);
				ensureAllFieldsAreOfSameType(type);
				packedComponentCheck(type);
			} else {
				validate(type);
			}
		}
		
		return false;
	}
	
	private void ensureAllFieldsAreOfSameType(TypeElement type) {
		Set<String> types = new HashSet<String>();
		for (VariableElement field : fieldsIn(type.getEnclosedElements())) {
			Set<Modifier> modifiers = field.getModifiers();
			if (modifiers.contains(Modifier.PRIVATE))
				types.add(field.asType().toString());
		}
		
		if (types.size() > 1) {
			Messager messager = processingEnv.getMessager();
			messager.printMessage(ERROR, "All fields must be of same type, found: " + types);
		}
	}

	private void validate(TypeElement component) {
		Types typeUtils = processingEnv.getTypeUtils();
		
		TypeMirror superclass = null;
		do {
			superclass = component.getSuperclass();
			if (PACKED_COMPONENT.equals(superclass.toString())) {
				packedComponentCheck(component);
				break;
			} else if (POOLED_COMPONENT.equals(superclass.toString())) {
				pooledComponentCheck(component);
				break;
			} else {
				component = (TypeElement)typeUtils.asElement(superclass);
			}
		} while (!COMPONENT.equals(superclass.toString()));
	}

	private void packedComponentCheck(TypeElement component) {
		ensureZeroArgConstructor(component);
		ensureNoFinalInstanceFields(component);
		ensureAllInstanceFieldsArePrivate(component);
	}

	private void ensureAllInstanceFieldsArePrivate(TypeElement component) {
		for (VariableElement field : fieldsIn(component.getEnclosedElements())) {
			Set<Modifier> modifiers = field.getModifiers();
			if (!modifiers.contains(PRIVATE) && !modifiers.contains(FINAL) && !modifiers.contains(STATIC)) {
				Messager messager = processingEnv.getMessager();
				messager.printMessage(ERROR, "All instance fields must be private", field);
			}
		}
	}

	private void pooledComponentCheck(TypeElement component) {
		ensureZeroArgConstructor(component);
		ensureNoFinalInstanceFields(component);
		checkIfPooledCanBePacked(component);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
	
	private void checkIfPooledCanBePacked(TypeElement component) {
		if (fieldsIn(component.getEnclosedElements()).isEmpty()) {
			Messager messager = processingEnv.getMessager();
			messager.printMessage(MANDATORY_WARNING, "Component can safely be converted to PackedComponent.", component);
		}
	}
	
	private void ensureZeroArgConstructor(TypeElement component) {
		for (ExecutableElement constructor : constructorsIn(component.getEnclosedElements())) {
			if (constructor.getParameters().isEmpty())
				return;
		}
		
		// it's still possible that a zero-arg constructor is generated by lombok or similar
		if (element(component).hasAnnotation(ONE_OF, "@lombok.NoArgsConstructor", "@lombok.RequiredArgsConstructor."))
			return;
		
		Messager messager = processingEnv.getMessager();
		messager.printMessage(ERROR, "Missing zero-argument constructor.", component);
	}
	
	private void ensureNoFinalInstanceFields(TypeElement component) {
		for (VariableElement field : fieldsIn(component.getEnclosedElements())) {
			Set<Modifier> modifiers = field.getModifiers();
			if (modifiers.contains(FINAL) && !modifiers.contains(STATIC)) {
				Messager messager = processingEnv.getMessager();
				messager.printMessage(ERROR, "Instance fields must not be declared final.", field);
			}
		}
	}
	
	private void ensureTypeExtendsComponent(TypeElement component) {
		Types typeUtils = processingEnv.getTypeUtils();
		TypeMirror superclass = null;
		
		do {
			superclass = component.getSuperclass();
			if (PACKED_COMPONENT.equals(superclass.toString()) || POOLED_COMPONENT.equals(superclass.toString())) {
				processingEnv.getMessager().printMessage(ERROR, "Weaved components must extend com.artemis.Component", component);
				break;
			} else if (COMPONENT.equals(superclass.toString())) {
				break;
			} else {
				component = (TypeElement)typeUtils.asElement(superclass);
			}
		} while (!"java.lang.Object".equals(superclass.toString()));
	}
}
