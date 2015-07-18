package com.artemis;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


final class ProcessorUtil {
	
	private static DeclaredType factoryInterface;
	private static TypeElement  ObjectElement;
	private static Types types;
	private static Elements elements;


	public static void init(ProcessingEnvironment env) {
		types = env.getTypeUtils();
		elements = env.getElementUtils();
		factoryInterface = types.getDeclaredType(
				elements.getTypeElement("com.artemis.EntityFactory"),
				types.getWildcardType(null, null));
		ObjectElement = (TypeElement) types.getDeclaredType(
				elements.getTypeElement("java.lang.Object")).asElement();
	}
	
	public static List<ExecutableElement> componentMethods(TypeElement element) {
		List<Element> allMembers = new ArrayList<Element>(); 
		allMembers.addAll(elements.getAllMembers(element));
		allMembers.removeAll(elements.getAllMembers(ObjectElement));
		allMembers.removeAll(
				elements.getAllMembers((TypeElement) factoryInterface.asElement()));
		
		List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
		for (Element e : allMembers) {
			if (e.getKind() == ElementKind.METHOD)
				methods.add((ExecutableElement) e);
		}
		
		return methods;
	}
	
	public static Set<TypeElement> parentInterfaces(TypeElement main) {
		Set<TypeElement> interfaces = new HashSet<TypeElement>();
		interfaces.add(main);
		parentInterfaces(main.getInterfaces(), interfaces);
		return interfaces;
		
	}
	
	private static void parentInterfaces(List<? extends TypeMirror> interfaceMirrors,
			Set<TypeElement> target) {
		
		for (TypeMirror mirror : interfaceMirrors) {
			TypeElement found = (TypeElement) ((DeclaredType) mirror).asElement();
			target.add(found);
			parentInterfaces(found.getInterfaces(), target);
		}
	}
	
	public static boolean isString(TypeMirror mirror) {
		if (!(mirror instanceof DeclaredType))
			return false;
		
		DeclaredType type = (DeclaredType) mirror;
		Element e = type.asElement();
		if (!(e instanceof TypeElement))
			return false;
		
		TypeElement typeElement = (TypeElement) e;
		
		return typeElement.getQualifiedName().toString().equals("java.lang.String");
	}

	public static boolean isEnum(TypeMirror mirror) {
		if (!(mirror instanceof DeclaredType))
			return false;

		DeclaredType type = (DeclaredType) mirror;
		return ElementKind.ENUM == type.asElement().getKind();
	}

	public static DeclaredType findFactory(TypeElement klazz) {
		
		
		while (true) {
			for (TypeMirror declared : klazz.getInterfaces()) {
				if (types.isSubtype(declared, factoryInterface))
					return (DeclaredType) declared;
			}
			
			return null;
		}
	}
	
	/**
	 * Finds and returns the requested annotation mirror, or null if element
	 * is not not annotated with requested annotation.
	 * 
	 * @param annotation
	 *            Annotation class.
	 * @param element
	 *            Element to extract mirror from.
	 * @return Matching mirror, or null if not present.
	 */
	public static AnnotationMirror mirror(String annotation, Element element) {
		if (!annotation.startsWith("@"))
			annotation = "@" + annotation;
		
		for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (mirror.toString().startsWith(annotation))
				return mirror;
		}
		return null;
	}
	
	/**
	 * Finds and returns the requested annotation mirror, or null if element
	 * is not not annotated with requested annotation.
	 * 
	 * @param annotation
	 *            Annotation class.
	 * @param element
	 *            Element to extract mirror from. 
	 * @return Matching mirror, or null if not present.
	 */
	public static AnnotationMirror mirror(Class<? extends Annotation> annotation, Element element) {
		return mirror("@" + annotation.getName(), element);
	}
	
	public static boolean hasMirror(String annotation, Element element) {
		return mirror(annotation, element) != null;
	}

	public static boolean hasMethod(TypeElement component, String setterMethod) {
		for (Element e : component.getEnclosedElements()) {
			if (nameMatches(e, setterMethod))
				return true;
		}
		
		return false;
	}
	
	public static boolean hasMethod(TypeElement component, String setterMethod,
			List<? extends VariableElement> params) {
		
		for (Element e : component.getEnclosedElements()) {
			if (!(e instanceof ExecutableElement))
				continue;
			
			ExecutableElement method = (ExecutableElement) e;
			if (nameMatches(e, setterMethod) && typesEqual(params, method.getParameters()))
				return true;
		}
		
		return false;
	}
	
	private static boolean typesEqual(List<? extends VariableElement> params,
			List<? extends VariableElement> params2) {
		
		if (params.size() != params2.size())
			return false;
		
		for (int i = 0; params.size() > i; i++){
			if (!params.get(i).asType().equals(params2.get(i).asType()))
				return false;
		}
		
		return true;
	}

	private static boolean nameMatches(Element e, String setterMethod) {
		return e.getKind() == ElementKind.METHOD && e.getSimpleName().toString().equals(setterMethod);
	}

}
