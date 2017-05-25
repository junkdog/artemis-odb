package com.artemis.utils.reflect;

import com.artemis.*;
import com.artemis.utils.IntBag;

import java.util.Arrays;

import static com.artemis.utils.reflect.ClassReflection.getMethod;
import static com.artemis.utils.reflect.ClassReflection.isInstance;

public final class ReflectionUtil {
	private static final Class<?>[] PARAM_ID = {int.class};
	private static final Class<?>[] PARAM_IDS = {IntBag.class};

	private ReflectionUtil() {}

	public static boolean implementsAnyObserver(BaseEntitySystem owner) {
		// manager/entity system check no longer needed, different hierarchy.
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
}
