package com.artemis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes({"com.artemis.annotations.CRef", "com.artemis.annotations.MRef"})
public class EntityFactoryProcessor extends AbstractProcessor {
	
	private Filer filer;
	private ModelFormatter formatter;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		filer = processingEnv.getFiler();
		formatter = new ModelFormatter();
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver() || types.size() == 0)
			return true;
		
		
		Set<TypeElement> factoryTypes = new HashSet<TypeElement>();
		for (Iterator<? extends TypeElement> iterator = types.iterator(); iterator.hasNext(); ) {
			factoryTypes.addAll(resolveTypes(roundEnv.getElementsAnnotatedWith(iterator.next())));
		}
		for (TypeElement factory : factoryTypes) {
			FactoryModel fm = new FactoryModel(factory, processingEnv);
			System.out.println(fm);
			generateSourceFile(fm);
		}
		
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
	
	private void generateSourceFile(FactoryModel model) {
		try {
			System.out.println(formatter.generate(model));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
