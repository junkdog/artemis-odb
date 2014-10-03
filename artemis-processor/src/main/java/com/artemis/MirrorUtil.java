package com.artemis;

import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

final class MirrorUtil {
	
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
