package com.artemis;

import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;
import com.artemis.annotations.UnstableApi;
import com.artemis.annotations.UseSetter;

/**
 * Entity factories offer a simple, but efficient method for creating entities with
 * a predefined component composition. Each factory is backed by an Archetype instance,
 * ensuring fast insertion into Entity Systems.
 *
 * Artemis-odb implements your factory interface automatically after configuring the
 * annotation processor. For more details, see:
 * https://github.com/junkdog/artemis-odb/wiki/Annotation-Processor
 * https://github.com/junkdog/artemis-odb/wiki/EntityFactory
 *
 * <pre>
 * {@literal @}Bind({Sprite.class, Cullible.class, Position.class, Velocity.class,
 * Asset.class, Size.class, HitPoints.class})
 * public interface ShipShortWire extends EntityFactory<ShipShortWire> {
 *     // method name maps Position
 *     ShipShortWire position(float x, float y);
 *     // parameter names must match field or setter name
 *     ShipShortWire velocity(float x, float y);
 *     ShipShortWire asset(String path);
 *     ShipShortWire size(float width, float height);
 *     {@literal @}Sticky ShipShortWire hitPoints(int current);
 * }
 * </pre>
 *
 * @param <T>
 * 		Factory type.
 * @see Bind
 * @see Sticky
 * @see UseSetter
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 *
 * @deprecated type-erasure, singular entities, prone to erroneous state
 * via annotation-processors - this feature is best retired.
 */
@Deprecated
public interface EntityFactory<T> {

	/**
	 * Creates a new factory based on this instance, inheriting
	 * any stickied values.
	 * @return This factory for chaining
	 */
	T copy();

	/**
	 * @param tag
	 * @return This factory for chaining
	 */
	T tag(String tag);

	/**
	 * Assigns entity to supplied group(s) upon creation.
	 * @param group
	 * 		Adds entity to this group.
	 * @return This factory for chaining
	 */
	T group(String group);


	/**
	 * Assigns entity to supplied group(s) upon creation.
	 * @param groupA
	 * 		Adds entity to this group.
	 * @param groups
	 * 		Adds entity to this group.
	 * @return This factory for chaining
	 */
	T group(String groupA, String... groups);

	/**
	 * Assigns entity to supplied group(s) upon creation.
	 * @param groupA
	 * 		Adds entity to this group.
	 * @param groupB
	 * 		Adds entity to this group.
	 * @param groups
	 * 		Adds entity to this group.
	 * @return This factory for chaining
	 */
	T group(String groupA, String groupB, String... groups);

	/**
	 * Creates a new Entity.
	 * @return created entity.
	 */
	Entity create();
}
