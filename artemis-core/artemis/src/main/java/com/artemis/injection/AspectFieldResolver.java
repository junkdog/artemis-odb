package com.artemis.injection;

import com.artemis.*;
import com.artemis.annotations.*;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.Field;

import java.util.IdentityHashMap;

import static com.artemis.Aspect.all;

/**
 * <p>Resolves the following aspect-related types:</p>
 * <ul>
 * <li>{@link com.artemis.Aspect}</li>
 * <li>{@link com.artemis.Aspect.Builder}</li>
 * <li>{@link com.artemis.EntitySubscription}</li>
 * <li>{@link com.artemis.EntityTransmuter}</li>
 * </ul>
 *
 * @author Snorre E. Brekke
 * @author Adrian Papari
 */
public class AspectFieldResolver implements FieldResolver {

    private static final Class<? extends Component>[] EMPTY_COMPONENT_CLASS_ARRAY = new Class[0];

    private World world;

    private IdentityHashMap<Field, Aspect.Builder> fields = new IdentityHashMap<Field, Aspect.Builder>();

    @Override
    public void initialize(World world) {
        this.world = world;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolve(Object target, Class<?> fieldType, Field field) {
        Aspect.Builder aspect = aspect(field);
        if (aspect == null)
            return null;

        if (Aspect.class == fieldType) {
            return world.getAspectSubscriptionManager().get(aspect).getAspect();
        } else if (Aspect.Builder.class == fieldType) {
            return aspect;
        } else if (EntityTransmuter.class == fieldType) {
            return new EntityTransmuter(world, aspect);
        } else if (EntitySubscription.class == fieldType) {
            return world.getAspectSubscriptionManager().get(aspect);
        } else if (Archetype.class == fieldType) {
            return new ArchetypeBuilder()
                    .add(allComponents(field))
                    .build(world);
        }

        return null;
    }

    private Aspect.Builder aspect(Field field) {
        if (!fields.containsKey(field)) {
            AspectDescriptor descriptor = descriptor(field);

            if (descriptor != null) {
                fields.put(field, toAspect(descriptor));
            } else {
                final All all = field.getAnnotation(All.class);
                final One one = field.getAnnotation(One.class);
                final Exclude exclude = field.getAnnotation(Exclude.class);

                if (all != null || one != null || exclude != null) {
                    fields.put(field, toAspect(all, one, exclude));
                } else {
                    fields.put(field, null);
                }
            }
        }

        return fields.get(field);
    }

    private AspectDescriptor descriptor(Field field) {
        Annotation anno = field.getDeclaredAnnotation(AspectDescriptor.class);
        return (anno != null)
                ? anno.getAnnotation(AspectDescriptor.class)
                : null;
    }

    private Aspect.Builder toAspect(AspectDescriptor ad) {
        return all(ad.all()).one(ad.one()).exclude(ad.exclude());
    }

    private Aspect.Builder toAspect(All all, One one, Exclude exclude) {
        return all(all != null ? all.value() : EMPTY_COMPONENT_CLASS_ARRAY)
                .one(one != null ? one.value() : EMPTY_COMPONENT_CLASS_ARRAY)
                .exclude(exclude != null ? exclude.value() : EMPTY_COMPONENT_CLASS_ARRAY);
    }

    private Class<? extends Component>[] allComponents(Field field) {
        AspectDescriptor descriptor = descriptor(field);

        if (descriptor != null) {
            return descriptor.all();
        } else {
            All all = field.getAnnotation(All.class);

            if (all != null) {
                return all.value();
            }
        }

        return null;
    }

}
