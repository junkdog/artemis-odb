package com.artemis;

import com.artemis.injection.AspectFieldResolver;
import com.artemis.injection.FieldResolver;
import com.artemis.utils.reflect.Field;

/**
 * Plugin to enable fluid entity functionality in your world.
 *
 * - Enables fluid entity functionality.
 * - Adds support for {@code com.artemis.ESubscription} field dependency injection.
 *   (when annotated with {@code @com.artemis.annotations.All}, {@code @com.artemis.annotations.One} and/or {@code @com.artemis.annotations.Exclude})
 * <p>
 * This file is generated.
 * <p>
 * For artemis-odb developers: Make sure you edit the file in  artemis-fluid-core-utility-sources, and not a
 * generated-sources version.
 */
public final class FluidEntityPlugin implements ArtemisPlugin {
    public void setup(WorldConfigurationBuilder b) {
        b.dependsOn(WorldConfigurationBuilder.Priority.HIGH, SuperMapper.class);
        b.register(new ESubscriptionAspectResolver());
    }

    /**
     *  Resolver with support for ESubscription.
     */
    private static class ESubscriptionAspectResolver implements FieldResolver {

        // we need to delegate to the aspect field resolver.
        private AspectFieldResolver aspectFieldResolver = new AspectFieldResolver();

        @Override
        public void initialize(World world) {
            aspectFieldResolver.initialize(world);
        }

        @Override
        public Object resolve(Object target, Class<?> fieldType, Field field) {
            if (ESubscription.class == fieldType) {
                return new ESubscription(((EntitySubscription) aspectFieldResolver.resolve(target, EntitySubscription.class, field)));
            }
            return null;
        }
    }
}
