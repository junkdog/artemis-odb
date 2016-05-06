package com.artemis.link;

import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

final class MutatorUtil {
	private MutatorUtil() {}

	static <T> T getGeneratedMutator(LinkSite linkSite) {
		Class[] possibleMutators = linkSite.field.getDeclaringClass().getDeclaredClasses();
		String mutatorName = "Mutator_" + linkSite.field.getName();
		for (int i = 0, s = possibleMutators.length; s > i; i++) {
			if (mutatorName.equals(possibleMutators[i].getSimpleName())) {
				try {
					return  (T) ClassReflection.newInstance(possibleMutators[i]);
				} catch (ReflectionException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return null;
	}

}
