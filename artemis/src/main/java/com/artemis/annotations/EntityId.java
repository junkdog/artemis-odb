package com.artemis.annotations;

import com.artemis.Entity;
import com.artemis.link.EntityLinkManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Marks <code>int</code> and {@link IntBag} fields as holding entity id:s.
 * Only works on component types. This annotation ensures that:
 * <ul>
 *     <li>Entity references can be safely serialized</li>
 *     <li>Tracks inter-entity relationships, if the {@link EntityLinkManager}
 *         is registed with the world.</li>
 * </ul>
 *
 * <p>{@link Bag} of {@link Entity} and plain <code>Entity</code> fields
 * don't need to be annotated.</p>
 *
 * @see <a href="https://github.com/junkdog/artemis-odb/wiki/Entity-References-and-Serialization">Entity References and Serialization</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityId {
}
