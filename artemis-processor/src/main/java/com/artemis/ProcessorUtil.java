package com.artemis;

import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

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
}
