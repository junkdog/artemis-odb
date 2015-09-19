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
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;
import com.artemis.annotations.UseSetter;

public class FactoryModel {
	private final Set<TypeElement> components = new HashSet<TypeElement>();
	private final List<FactoryMethod> methods;
	final TypeElement declaration;
	private final Map<String, TypeElement> autoResolvable;
	private final ProcessingEnvironment env;
	private Messager messager;
	boolean success = true;
	
	private static final List<String> IGNORED_METHODS = Arrays.asList("getClass", "wait", "notify", "notifyAll", "equals",
			"hashCode", "equals", "toString", "copy",
			"create", "tag", "group");
	
	FactoryModel(TypeElement declaration, ProcessingEnvironment env) {
		this.declaration = declaration;
		this.env = env;
		messager = env.getMessager();
		
		autoResolvable = new HashMap<String, TypeElement>();
		for (TypeElement parent : ProcessorUtil.parentInterfaces(declaration)) {
			autoResolvable.putAll(readGlobalCRefs(parent));
		}
		autoResolvable.putAll(readGlobalCRefs(declaration));
		
		// if something overrides
		
		methods = scanMethods(declaration);
		validate();
	}
	
	private void validate() {
		DeclaredType factory = ProcessorUtil.findFactory(declaration);
		if (factory == null) {
			success = false;
			messager.printMessage(ERROR, "Interface must extend com.artemis.EntityFactory", declaration);
			return;
		}
		
		// if empty, we're probably extending an existing factory
		if (factory.getTypeArguments().size() > 0) { 
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
		}
		
		for (FactoryMethod method : methods)
			success &= method.validate(messager);
	}
	
	public List<FactoryMethod> getStickyMethods() {
		List<FactoryMethod> m = new ArrayList<FactoryMethod>();
		for (FactoryMethod fm : methods)
			if (fm.sticky && fm.setterMethod == null) m.add(fm);
		
		return m;
	}
	
	public List<FactoryMethod> getInstanceMethods() {
		List<FactoryMethod> m = new ArrayList<FactoryMethod>();
		for (FactoryMethod fm : methods)
			if (!fm.sticky && fm.setterMethod == null) m.add(fm);
		
		return m;
	}
	
	public List<FactoryMethod> getSetterMethods() {
		List<FactoryMethod> m = new ArrayList<FactoryMethod>();
		for (FactoryMethod fm : methods)
			if (!fm.sticky && fm.setterMethod != null) m.add(fm);
		
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
//		Elements util = env.getElementUtils();
//		return factoryMethods(util.getAllMembers(factory));
		return factoryMethods(ProcessorUtil.componentMethods(factory));
	}
	
	private List<FactoryMethod> factoryMethods(List<ExecutableElement> allMembers) {
		List<FactoryMethod> methods = new ArrayList<FactoryMethod>();
		for (ExecutableElement e : allMembers) {
			String elementName = e.getSimpleName().toString();

			if (!IGNORED_METHODS.contains(elementName)) {
				FactoryMethod method = factoryMethod(e);
				if (method != null) {
					methods.add(method);
				} else {
					success = false;
				}
			}
			else {
				if (!readCRef(e).isEmpty()) {
					String err = "Invalid method name for factory method";
					messager.printMessage(Kind.ERROR, err, e);
					success = false;
				}
			}
		}
		return methods;
	}

	private FactoryMethod factoryMethod(ExecutableElement e) {
		String elementName = e.getSimpleName().toString();

		List<AnnotationValue> referenced = readCRef(e);
		if (referenced.size() == 0) {
			if (autoResolvable.containsKey(elementName)) {
				return new FactoryMethod(e, autoResolvable.get(elementName));
			} else {
				String err = "Unable to match component for " + e.getSimpleName();
				messager.printMessage(Kind.ERROR, err, e);
				return null;
			}
		} else if (referenced.size() == 1) {
			return bindMethod(e, (DeclaredType)referenced.get(0).getValue());
		} else {
			String err = "@Bind on methods limited to one component type, found " + referenced.size();
			messager.printMessage(Kind.ERROR, err, e);
			return null;
		}
	}

	private FactoryMethod bindMethod(ExecutableElement method, DeclaredType value) {
		TypeElement component = (TypeElement)value.asElement();
		components.add(component);
		
		// scan for UseSetter
		String setterMethod = null;
		AnnotationMirror setterMirror = ProcessorUtil.mirror(UseSetter.class, method);
		if (setterMirror != null) {
			AnnotationValue setter = readAnnotationField(setterMirror, "value");
			setterMethod = (setter != null) 
					? (String)setter.getValue()
					: method.getSimpleName().toString();
		}
		
		return new FactoryMethod(method, component, setterMethod);
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
		private final String setterMethod;
		
		private FactoryMethod(ExecutableElement method, TypeElement component) {
			this(method, component, null);
		}
		
		private FactoryMethod(ExecutableElement method, TypeElement component, String setterMethod) {
			assert(method != null);
			assert(component != null);
			this.method = method;
			this.sticky = method.getAnnotation(Sticky.class) != null;
			this.component = component;
			this.setterMethod = setterMethod;
			
			params = map(method.getParameters());
		}
		
		// refactor into own class
		boolean validate(Messager messager) {
			Map<Name, Element> found = map(component.getEnclosedElements());
			boolean success = true;
			
			for (Entry<Name, VariableElement> param : params.entrySet()) {
				if (setterMethod == null)
					success &= validateFieldAccess(messager, found, param);
				else // invoking setter
					success &= validateSetterAccess(messager, found, param);
			}
			
			return success;
		}
		
		private boolean validateFieldAccess(Messager messager,
				Map<Name, Element> found,	Entry<Name, VariableElement> param) {
			
			if (!found.containsKey(param.getKey())) {
				messager.printMessage(
						ERROR,
						format("%s has no field named %s", component.getSimpleName(), param.getKey()),
						param.getValue());
				
				return false;
			}

			if (!isParameterValid(param)) {
				messager.printMessage(
						ERROR,
						"Only primitive, enum and string types supported",
						param.getValue());
				
				return false;
			}
			
			return true;
		}

		private boolean isParameterValid(Entry<Name, VariableElement> param) {
			VariableElement value = param.getValue();
			TypeMirror type = value.asType();

			return type.getKind().isPrimitive()
					|| ProcessorUtil.isEnum(type)
					|| ProcessorUtil.isString(type);
		}

		private boolean validateSetterAccess(Messager messager,
				Map<Name, Element> found,	Entry<Name, VariableElement> param) {
			
			// component has method
			if (!ProcessorUtil.hasMethod(component, setterMethod)) {
				messager.printMessage(
						ERROR,
						format("Expected to find method '%s' in component '%s'",
							setterMethod, component.getSimpleName()),
						method);
				
				return false;
			}
			
			// validate parameter list
			if (!ProcessorUtil.hasMethod(component, setterMethod, method.getParameters())) {
				StringBuilder signature = new StringBuilder();
				for (VariableElement e : method.getParameters()) {
					if (signature.length() > 0) signature.append(", ");
					signature.append(e.asType().toString());
				}
				
				messager.printMessage(
						ERROR,
						format("Expected to find %s.%s(%s)'",
							component.getSimpleName(), setterMethod, signature),
						method);
				
				return false;
			}
			
			return true;
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
			return getParamsFull(method.getParameters());
		}
		
		private String getParamsFull(List<? extends VariableElement> parameters) {
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
		
		public String getParamArgs() {
			StringBuilder params = new StringBuilder();
			String delim = "";
			for (VariableElement param : method.getParameters()) {
				params.append(delim);
				params.append(new Param(component, param).field);
				delim = ", ";
			}
			
			return params.toString();
		}
		
		public String getSetter() {
			return setterMethod;
		}

		@Override
		public String toString() {
			String stickied = sticky ? "@Sticky " : "";
			String cref = "@CRef(" + component.getSimpleName() + ".class) ";
			String params = getParamsFull(method.getParameters());
			
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
