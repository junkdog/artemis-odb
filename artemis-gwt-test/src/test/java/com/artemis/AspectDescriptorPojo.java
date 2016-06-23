package com.artemis;

import com.artemis.annotations.AspectDescriptor;
import com.artemis.component.*;

public class AspectDescriptorPojo {
	@AspectDescriptor(
		all = {ComponentX.class, ComponentY.class},
		exclude = PooledString.class,
		one = {ReusedComponent.class, EntityHolder.class})
	public EntitySubscription sub;

	@AspectDescriptor(
		all = {ComponentX.class, ComponentY.class},
		exclude = PooledString.class,
		one = {ReusedComponent.class, EntityHolder.class})
	public EntityTransmuter transmuter;

	@AspectDescriptor(
		all = {ComponentX.class, ComponentY.class},
		exclude = PooledString.class,
		one = {ReusedComponent.class, EntityHolder.class})
	public Aspect aspect;

	@AspectDescriptor(
		all = {ComponentX.class, ComponentY.class},
		exclude = PooledString.class,
		one = {ReusedComponent.class, EntityHolder.class})
	public Aspect.Builder ab;

	@AspectDescriptor(
		all = {ComponentX.class, ReusedComponent.class})
	public Archetype archetype;
}
