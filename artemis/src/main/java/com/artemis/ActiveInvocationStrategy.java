package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.BitSet;

import static com.artemis.utils.reflect.ClassReflection.isAnnotationPresent;

public class ActiveInvocationStrategy extends SystemInvocationStrategy {

	private BitSet active = new BitSet();

	@Override
	protected void initialize() {
		ImmutableBag<BaseSystem> systems = world.getSystems();
		for (int i = 0; i < systems.size(); i++) {
			BaseSystem system = systems.get(i);
			if (!isAnnotationPresent(system.getClass(), PassiveSystem.class))
				active.set(i);
		}
	}

	@Override
	protected void process(Bag<BaseSystem> systems) {
		Object[] systemsData = systems.getData();
		for (int i = 0, s = systems.size(); s > i; i++) {
			BaseSystem system = (BaseSystem) systemsData[i];
			if (active.get(i)) {
				system.process();
				updateEntityStates();
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface PassiveSystem {}
}
