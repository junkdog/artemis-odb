package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

import static com.artemis.annotations.LinkPolicy.Policy.SKIP;

public class LinkFactory {
    private static final int NULL_REFERENCE = 0;
    private static final int SINGLE_REFERENCE = 1;
    private static final int MULTI_REFERENCE = 2;

    private final Bag<LinkSite> links = new Bag<LinkSite>();
    private final World world;

    private final ReflexiveMutators reflexiveMutators;

    public LinkFactory(World world) {
        this.world = world;
        reflexiveMutators = world.getReflextiveMutators();
    }

    public static boolean isGenericType(Field f, Class<?> mainType, Class typeParameter) {
        return mainType == f.getType() && typeParameter == f.getElementType(0);
    }

    static int getReferenceTypeId(World world, Field f) {
        Class type = f.getType();
        if (world.getEntityClass() != null) {
            if (world.getEntityClass() == type)
                return SINGLE_REFERENCE;
            if (isGenericType(f, Bag.class, world.getEntityClass()))
                return MULTI_REFERENCE;
        }

        boolean explicitEntityId = f.getDeclaredAnnotation(EntityId.class) != null;
        if (int.class == type && explicitEntityId)
            return SINGLE_REFERENCE;
        if (IntBag.class == type && explicitEntityId)
            return MULTI_REFERENCE;

        return NULL_REFERENCE;
    }

    Bag<LinkSite> create(ComponentType ct) {
        Class<?> type = ct.getType();
        Field[] fields = ClassReflection.getDeclaredFields(type);

        links.clear();
        for (int i = 0; fields.length > i; i++) {
            Field f = fields[i];
            int referenceTypeId = getReferenceTypeId(world, f);
            if (referenceTypeId != NULL_REFERENCE && (SKIP != getPolicy(f))) {
                if (SINGLE_REFERENCE == referenceTypeId) {
                    UniLinkSite ls = new UniLinkSite(world, ct, f);
                    if (!configureMutator(ls))
                        reflexiveMutators.withMutator(ls);

                    links.add(ls);
                } else if (MULTI_REFERENCE == referenceTypeId) {
                    MultiLinkSite ls = new MultiLinkSite(world, ct, f);
                    if (!configureMutator(ls))
                        reflexiveMutators.withMutator(ls);

                    links.add(ls);
                }
            }
        }

        return links;
    }

    static LinkPolicy.Policy getPolicy(Field f) {
        Annotation annotation = f.getDeclaredAnnotation(LinkPolicy.class);
        if (annotation != null) {
            LinkPolicy lp = annotation.getAnnotation(LinkPolicy.class);
            return lp != null ? lp.value() : null;
        }

        return null;
    }

    private boolean configureMutator(UniLinkSite linkSite) {
        UniFieldMutator mutator = MutatorUtil.getGeneratedMutator(linkSite);
        if (mutator != null) {
            mutator.setWorld(world);
            linkSite.fieldMutator = mutator;
            return true;
        } else {
            return false;
        }
    }

    private boolean configureMutator(MultiLinkSite linkSite) {
        MultiFieldMutator mutator = MutatorUtil.getGeneratedMutator(linkSite);
        if (mutator != null) {
            mutator.setWorld(world);
            linkSite.fieldMutator = mutator;
            return true;
        } else {
            return false;
        }
    }

    public static interface ReflexiveMutators {
        UniLinkSite withMutator(UniLinkSite linkSite);

        MultiLinkSite withMutator(MultiLinkSite linkSite);
    }
}
