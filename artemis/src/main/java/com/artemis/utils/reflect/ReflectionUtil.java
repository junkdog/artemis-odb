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

	public static boolean implementsAnyObserver(BaseSystem owner) {
		if (isInstance(Manager.class, owner) || isInstance(EntitySystem.class, owner))
			return true; // case handled by implementsObserver(owner, methodName)

		Class type = owner.getClass();
		while (type != BaseSystem.class) {
			for (Method m : ClassReflection.getDeclaredMethods(type)) {
				String name = m.getName();
				if ("inserted".equals(name) || "removed".equals(name)) {
					Class[] types = m.getParameterTypes();
					Class declarer = m.getDeclaringClass();
					if (Arrays.equals(PARAM_ID, types) || Arrays.equals(PARAM_IDS, types)) {
						if (!BaseEntitySystem.class.equals(declarer))
							return true;
					}
				}
			}

			type = type.getSuperclass();
		}

		return false;
	}

	public static boolean isGenericType(Field f, Class<?> mainType, Class typeParameter) {
		return mainType == f.getType() && typeParameter == f.getElementType(0);
	}
}
