package com.artemis;

import com.artemis.injection.*;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Daan van Yperen
 */
public class PojoFieldResolverTest {

	@Test(expected = InjectionException.class)
	public void custom_field_handler_without_field_resolver_should_fail_if_any_injectables() {

		WorldConfiguration configuration =
				new WorldConfiguration().setInjector(createInjectorWithCustomHandler());
		configuration.register(new BlankObject());

		new World(configuration);
	}

	@Test
	public void custom_field_handler_without_field_resolver_should_not_fail_if_no_injectables() {
		WorldConfiguration configuration =
				new WorldConfiguration().setInjector(createInjectorWithCustomHandler());
		new World(configuration);
	}

	@Test
	public void pojo_field_resolver_should_be_supplied_with_any_injectables() {
		PojoFieldResolverImpl pojoFieldResolver = new PojoFieldResolverImpl();
		WorldConfiguration configuration =
				new WorldConfiguration().setInjector(createInjectorWithCustomHandler(pojoFieldResolver));
		configuration.register("a",new BlankObject());
		configuration.register("b",new BlankObject());
		new World(configuration);
		assertEquals(2, pojoFieldResolver.pojos.size());
	}

	private Injector createInjectorWithCustomHandler(FieldResolver... resolvers ) {
		Injector myInjector = new CachedInjector();

		Bag<FieldResolver> resolverBag = new Bag<FieldResolver>(resolvers.length);
		for (FieldResolver resolver : resolvers) {
			resolverBag.add(resolver);
		}

		FieldHandler handler = new FieldHandler(new InjectionCache(), resolverBag);
		for (FieldResolver resolver : resolvers) {
			handler.addFieldResolver(resolver);
		}
		myInjector.setFieldHandler(handler);

		return myInjector;
	}


	private static class PojoFieldResolverImpl implements PojoFieldResolver {

		private Map<String, Object> pojos;

		public Map<String, Object> getPojos() {
			return pojos;
		}

		@Override
		public void setPojos(Map<String, Object> pojos) {
			this.pojos = pojos;
		}

		@Override
		public void initialize(World world) {

		}

		@Override
		public Object resolve(Object target, Class<?> fieldType, Field field) {
			return null;
		}
	}

	public static class BlankObject {
	}
}
