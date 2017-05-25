package com.artemis.injection;

public class SharedInjectionCache {
	protected InjectionCache initialValue() {
		return new InjectionCache();
	}

	public InjectionCache get() {
		return new InjectionCache();
	}
}
