package com.artemis;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

final class ProcessorUtil {
	
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
	
	public static DeclaredType findFactory(TypeElement klazz) {
		for (TypeMirror declared : klazz.getInterfaces()) {
			DeclaredType dt = (DeclaredType) declared;
			Name interfaceName = ((TypeElement)dt.asElement()).getQualifiedName();
			if ("com.artemis.EntityFactory".equals(interfaceName.toString()))
				return dt;
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
			if (!params.get(i).getSimpleName().equals(params2.get(i).getSimpleName()))
				return false;
		}
		
		return true;
	}

	private static boolean nameMatches(Element e, String setterMethod) {
		return e.getKind() == ElementKind.METHOD && e.getSimpleName().toString().equals(setterMethod);
	}
}
