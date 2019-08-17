package com.artemis.utils.reflect;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.One;

/**
 * Annotation reader for systems.
 *
 * @author Daan van Yperen
 */
public class SystemMetadata {
    private final Class<? extends BaseSystem> c;

    public SystemMetadata(Class<? extends BaseSystem> c) {
        this.c = c;
    }

    /**
     * Return aspect as defined in annotation.
     *
     * @return {@code Aspect.Builder} as defined in annotations, or {@code null} if none.
     */
    public Aspect.Builder getAspect() {
        final Aspect.Builder aspect = Aspect.all();
        final All all = ClassReflection.getAnnotation(c, All.class);
        if (all != null) {
            aspect.all(all.value());
        }
        final One one = ClassReflection.getAnnotation(c, One.class);
        if (one != null) {
            aspect.one(one.value());
        }
        final Exclude exclude = ClassReflection.getAnnotation(c, Exclude.class);
        if (exclude != null) {
            aspect.exclude(exclude.value());
        }
        return (all != null || exclude != null || one != null) ? aspect : null;
    }
}
