package com.artemis;

import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;


/**
 *
 * <pre>
 * 
 * {@literal @}Bind({Sprite.class, Cullible.class, Position.class, Velocity.class,
     Asset.class, Size.class, HitPoints.class})
 * public interface ShipShortWire extends EntityFactory<ShipShortWire> {
 *     // method name maps Position
 *     ShipShortWire position(float x, float y);
 *     // parameter names must match field or setter name
 *     ShipShortWire velocity(float x, float y);
 *     ShipShortWire asset(String path);
 *     ShipShortWire size(float width, float height);
 *     {@literal @}Sticky ShipShortWire hitPoints(int current);
 * }
 * 
 * </pre>
 * 
 *
 * @param <T> Factory type.
 */
public interface EntityFactory<T> {
	T copy();
	T tag(String tag);
	T group(String group);
	T group(String groupA, String... groups);
	T group(String groupA, String groupB, String... groups);
	Entity create();
}
