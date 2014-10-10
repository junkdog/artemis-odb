package com.artemis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes({
	"com.artemis.annotations.Bind",
	"com.artemis.annotations.UseSetter"})
public class EntityFactoryProcessor extends AbstractProcessor {
	
	private Filer filer;
	private ModelFormatter formatter;
	private Messager messager;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
		formatter = new ModelFormatter();
		
		ProcessorUtil.init(processingEnv);
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
			if (fm.success)
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
			String factoryName = model.declaration.getQualifiedName() + "Impl";
			JavaFileObject src = filer.createSourceFile(factoryName, model.declaration);
			PrintWriter writer = new PrintWriter(src.openWriter());
			writer.println(formatter.generate(model));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			messager.printMessage(Kind.ERROR, e.getMessage(), model.declaration);
			e.printStackTrace();
		}
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
