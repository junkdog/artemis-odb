package com.artemis;

import static java.lang.String.format;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;

public class FactoryModel {
	private final Set<TypeElement> components = new HashSet<TypeElement>();
	private final List<FactoryMethod> methods;
	final TypeElement declaration;
	private final Map<String, TypeElement> autoResolvable;
	private final ProcessingEnvironment env;
	private Messager messager;
	boolean success = true;
	
	private static final List<String> IGNORED_METHODS = Arrays.asList(new String[] {
			"getClass", "wait", "notify", "notifyAll", "equals",
			"hashCode", "equals", "toString", "copy",
			"create", "tag", "group"}); 
	
	FactoryModel(TypeElement declaration, ProcessingEnvironment env) {
		this.declaration = declaration;
		this.env = env;
		messager = env.getMessager();
		autoResolvable = readGlobalCRefs(declaration);
		
		readGlobalCRefs(declaration);
		methods = scanMethods(declaration);
		validate();
	}
	
	private void validate() {
		DeclaredType factory = ProcessorUtil.findFactory(declaration);
		if (factory == null) {
			success = false;
			messager.printMessage(ERROR, "Interface must implement com.artemis.EntityFactory", declaration);
			return;
		}
		
		DeclaredType argument = ((DeclaredType) factory.getTypeArguments().get(0));
		TypeElement factoryType = (TypeElement) argument.asElement();
		if (!factoryType.getQualifiedName().equals(declaration.getQualifiedName())) {
			success = false;
			messager.printMessage(ERROR,
					format("Expected EntityFactory<%s>, but found EntityFactory<%s>",
							declaration.getSimpleName(),
							factoryType.getSimpleName()),
					declaration);
		}
		
		for (FactoryMethod method : methods)
			method.validate(messager, env.getTypeUtils());
	}
	
	public List<FactoryMethod> getStickyMethods() {
		List<FactoryMethod> m = new ArrayList<FactoryMethod>();
		for (FactoryMethod fm : methods)
			if (fm.sticky) m.add(fm);
		
		return m;
	}
	
	public List<FactoryMethod> getInstanceMethods() {
		List<FactoryMethod> m = new ArrayList<FactoryMethod>();
		for (FactoryMethod fm : methods)
			if (!fm.sticky) m.add(fm);
		
		return m;
	}

	public String getPackageName() {
		String pkg = declaration.getEnclosingElement().toString();
		if (pkg.startsWith("package ")) pkg = pkg.substring("package ".length());
		return pkg;
	}
	
	public String getFactoryName() {
		return declaration.getSimpleName().toString();
	}
	
	public List<String> getComponents(boolean qualifiedName) {
		List<String> components = new ArrayList<String>();
		for (TypeElement c : this.components)
			components.add((qualifiedName ? c.getQualifiedName() : c.getSimpleName()).toString());
		
		return components;
	}
	
	public Set<String> getMappedComponents() {
		Set<String> components = new HashSet<String>();
		for (FactoryMethod m : this.methods)
			components.add(m.component.getSimpleName().toString());
		
		return components;
	}
	
	public Set<String> getFields() {
		Set<String> fields = new TreeSet<String>();
		for (FactoryMethod m : methods) {
			if (!m.sticky)
				fields.add("private boolean " + m.getFlagName());
			
			for (Param p : m.getParams()) {
				fields.add(format("private %s %s", p.type, p.field));
			}
		}
		
		return fields;
	}
	
	private List<FactoryMethod> scanMethods(TypeElement factory) {
		Elements util = env.getElementUtils();
		return factoryMethods(util.getAllMembers(factory));
	}
	
	private List<FactoryMethod> factoryMethods(List<? extends Element> allMembers) {
		List<FactoryMethod> methods = new ArrayList<FactoryMethod>();
		for (Element e : allMembers) {
			FactoryMethod method;
			if ((method = factoryMethod(e)) != null) {
				methods.add(method);
			}
		}
		return methods;
	}

	private FactoryMethod factoryMethod(Element e) {
		if (!(e instanceof ExecutableElement))
			return null;
		
		String elementName = e.getSimpleName().toString();
		if (IGNORED_METHODS.contains(elementName)) {
			if (!readCRef(e).isEmpty()) {
				String err = "Invalid method name for factory method";
				messager.printMessage(Kind.WARNING, err, e);
			}
			return null;
		}
		
		List<AnnotationValue> referenced = readCRef(e);
		if (referenced.size() == 0) {
			if (autoResolvable.containsKey(elementName)) {
				return new FactoryMethod((ExecutableElement)e, autoResolvable.get(elementName));
			} else {
				String err = "Unable to match component for " + e.getSimpleName();
				messager.printMessage(Kind.ERROR, err, e);
				return null;
			}
		} else if (referenced.size() == 1) {
			Object val = referenced.get(0).getValue();
			DeclaredType value = (DeclaredType)val;
			TypeElement component = (TypeElement)value.asElement();
			components.add(component);
			return new FactoryMethod((ExecutableElement)e, component);
		} else {
			String err = "@CRef on methods limited to one component type, found " + referenced.size();
			messager.printMessage(Kind.ERROR, err, e);
			return null;
		}
	}

	private Map<String, TypeElement> readGlobalCRefs(TypeElement declaration) {
		Map<String, TypeElement> autoResolvable = new HashMap<String, TypeElement>(); 
		for (AnnotationValue value : readCRef(declaration)) {
			TypeElement type = (TypeElement)((DeclaredType)value.getValue()).asElement();
			this.components.add(type);
			autoResolvable.put(key(type), type);
		}
		
		return autoResolvable;
	}


	@SuppressWarnings("unchecked")
	private static List<AnnotationValue> readCRef(Element element) {
		AnnotationMirror cref = ProcessorUtil.mirror(Bind.class, element);
		if (cref == null)
			return Collections.emptyList();
		
		AnnotationValue components = readAnnotationField(cref, "value");
		return (List<AnnotationValue>)components.getValue();
	}


	private static String key(TypeElement type) {
		String key = type.getSimpleName().toString();
		return key.toLowerCase().charAt(0) + key.substring(1);
	}
	
	static AnnotationValue readAnnotationField(AnnotationMirror annotation, String field) {
		for (ExecutableElement key : annotation.getElementValues().keySet()) {
			if (field.equals(key.getSimpleName().toString())) {
				return annotation.getElementValues().get(key);
			}
		}
		
		return null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FactoryModel:" + getPackageName() + declaration.getSimpleName() + "(\n");

		String delim = "";
		sb.append("\tarchetype=");
		for (TypeElement c : components) {
			sb.append(delim).append(c.getSimpleName());
			delim = ", ";
		}
		sb.append("\n");
		for (FactoryMethod m : this.methods) {
			sb.append('\t').append(m).append('\n');
		}
		sb.append(')');
		return sb.toString();
	}

	private static String camelCase(CharSequence s) {
		return Character.toLowerCase(s.charAt(0)) + s.toString().substring(1);
	}

	public static class FactoryMethod {
		public final boolean sticky;
		public final ExecutableElement method;
		public final TypeElement component;
		public final Map<Name, VariableElement> params;
		
		private FactoryMethod(ExecutableElement method, TypeElement component) {
			assert(method != null);
			assert(component != null);
			this.method = method;
			this.sticky = method.getAnnotation(Sticky.class) != null;
			this.component = component;
			params = map(method.getParameters());
		}
		
		boolean validate(Messager messager, Types types) {
			Map<Name, Element> found = map(component.getEnclosedElements());
			boolean success = true;
			
			for (Entry<Name, VariableElement> param : params.entrySet()) {
				if (!found.containsKey(param.getKey())) {
					success = false;
					messager.printMessage(
							ERROR,
							format("%s has no field named %s", component.getSimpleName(), param.getKey()),
							param.getValue());
				}
				
				TypeMirror type = param.getValue().asType();
				if (!(type.getKind().isPrimitive() || ProcessorUtil.isString(type))) {
					success = false;
					messager.printMessage(
							ERROR,
							"Only primitive and string types supported",
							param.getValue());
				}
			}
			
			return success;
		}
		
		private static <T extends Element> Map<Name, T> map(List<? extends T> elements) {
			Map<Name, T> map = new HashMap<Name, T>();
			for (T e : elements)
				map.put(e.getSimpleName(), e);
			
			return map;
		}

		public String getFlagName() {
			StringBuilder sb = new StringBuilder();
			sb.append("_id_").append(camelCase(method.getSimpleName())).append("_");
			for (VariableElement e : method.getParameters()) {
				sb.append(e.getSimpleName()).append("_");
			}
			return sb.toString();
		}
		
		public String getName() {
			return camelCase(method.getSimpleName());
		}
		
		public String getComponentName() {
			return component.getSimpleName().toString();
		}
		
		
		public String getParamsFull() {
			StringBuilder sb = new StringBuilder();
			for (VariableElement param : method.getParameters()) {
				if (sb.length() > 0) sb.append(", ");
				sb.append(param.asType() + " " + param.getSimpleName());
			}
			return sb.toString();
		}
		
		public List<Param> getParams() {
			List<Param> params = new ArrayList<Param>();
			for (VariableElement param : method.getParameters())
				params.add(new Param(component, param));
			
			return params;
		}
		
		@Override
		public String toString() {
			String stickied = sticky ? "@Sticky " : "";
			String cref = "@CRef(" + component.getSimpleName() + ".class) ";
			String params = getParamsFull();
			
			return "FactoryMethod [" + stickied + cref + method.getSimpleName() + "(" + params + ")]";
		}
	}
	
	public static class Param {
		private final String field;
		private final String param;
		private final String type;
		
		Param(TypeElement component, VariableElement param) {
			this.param = camelCase(param.getSimpleName());
			this.field = String.format("_%s_%s", camelCase(component.getSimpleName()), this.param);
			this.type = param.asType().toString();
		}
		
		public String getType() {
			return type;
		}

		public String getField() {
			return field;
		}

		public String getParam() {
			return param;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			result = prime * result + ((param == null) ? 0 : param.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Param other = (Param) obj;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			if (param == null) {
				if (other.param != null)
					return false;
			} else if (!param.equals(other.param))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}
