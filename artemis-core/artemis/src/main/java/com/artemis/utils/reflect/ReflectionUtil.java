package com.artemis.utils.reflect;

import com.artemis.*;
import com.artemis.utils.IntBag;

import java.util.Arrays;

import static com.artemis.utils.reflect.ClassReflection.getMethod;
import static com.artemis.utils.reflect.ClassReflection.isInstance;

public final class ReflectionUtil {
	private static final Class<?>[] PARAM_ENTITY = {Entity.class};
	private static final Class<?>[] PARAM_ID = {int.class};
	private static final Class<?>[] PARAM_IDS = {IntBag.class};

	private ReflectionUtil() {}

	public static boolean implementsObserver(BaseSystem owner, String methodName) {
		try {
			Method method = getMethod(owner.getClass(), methodName, PARAM_ENTITY);
			Class declarer = method.getDeclaringClass();
			return !(Manager.class.equals(declarer) || EntitySystem.class.equals(declarer));
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean implementsAnyObserver(BaseEntitySystem owner) {
		if (isInstance(Manager.class, owner) || isInstance(EntitySystem.class, owner))
			return true; // case handled by implementsObserver(owner, methodName)

		// check parent chain for user-supplied implementations of
		// inserted() and removed()
		Class type = owner.getClass();
		while (type != BaseEntitySystem.class) {
			for (Method m : ClassReflection.getDeclaredMethods(type)) {
				if (isObserver(m)) return true;
			}

			type = type.getSuperclass();
		}

		return false;
	}

	private static boolean isObserver(Method m) {
		String name = m.getName();
		if ("inserted".equals(name) || "removed".equals(name)) {
			Class[] types = m.getParameterTypes();
			if (Arrays.equals(PARAM_ID, types) || Arrays.equals(PARAM_IDS, types)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isGenericType(Field f, Class<?> mainType, Class typeParameter) {
		return mainType == f.getType() && typeParameter == f.getElementType(0);
	}
}
