package com.artemis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes({"com.artemis.annotations.CRef", "com.artemis.annotations.MRef"})
public class EntityFactoryProcessor extends AbstractProcessor {
	
	@Override
	public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver() || types.size() == 0)
			return true;
		
		Set<TypeElement> factoryTypes = new HashSet<TypeElement>();
		for (Iterator<? extends TypeElement> iterator = types.iterator(); iterator.hasNext(); ) {
			factoryTypes.addAll(resolveTypes(roundEnv.getElementsAnnotatedWith(iterator.next())));
		}
		System.out.println("FOUND classes :" + factoryTypes);
		
		return false;
	}
	
	private static Set<TypeElement> resolveTypes(Set<? extends Element> elements) {
		Set<TypeElement> factoryTypes = new HashSet<TypeElement>();
		for (Element e : elements) {
			if (e instanceof TypeElement)
				factoryTypes.add((TypeElement) e);
			else if (e instanceof ExecutableElement)
				factoryTypes.add((TypeElement) e.getEnclosingElement());
		}
		return factoryTypes;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
