package com.artemis;


import com.artemis.annotations.Wire;
import com.artemis.component.Asset;
import com.artemis.component.Complex;
import com.artemis.component.Cullible;
import com.artemis.component.HitPoints;
import com.artemis.component.Position;
import com.artemis.component.Sprite;
import com.artemis.factory.Extended;
import com.google.gwt.junit.client.GWTTestCase;

@Wire(injectInherited = false)
public class ExtendedEntityFactoryTest extends GWTTestCase {
	private static final double ACC = 0.001;
	
	private Extended factory;
	private World world;
	
	private ComponentMapper<Position> position;
	private ComponentMapper<Asset> asset;
	private ComponentMapper<Cullible> cullible;
	private ComponentMapper<Complex> complex;
	private ComponentMapper<HitPoints> hitpoints;
	private ComponentMapper<Sprite> sprite;
	
	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}
	
	@Override
	public void gwtSetUp() {
		world = new World();
		world.inject(this);
	}
	
	public void test_instance_methods() {
		int e1 = factory.sprite("s").asset("1").position(1, 2).create();
		int e2 = factory.sprite("h").position(3, 4).asset("2").create();
		int e3 = factory.create();
		
		assertEquals(1, position.get(e1).x, ACC);
		assertEquals(2, position.get(e1).y, ACC);
		assertEquals(3, position.get(e2).x, ACC);
		assertEquals(4, position.get(e2).y, ACC);
		assertEquals(0, position.get(e3).x, ACC);
		assertEquals(0, position.get(e3).y, ACC);
		
		assertEquals("1", asset.get(e1).path);
		assertEquals("2", asset.get(e2).path);
		assertEquals(null, asset.get(e3).path);
		assertEquals("s", sprite.get(e1).dummy);
		assertEquals("h", sprite.get(e2).dummy);
		assertEquals(null, sprite.get(e3).dummy);
	}
	
	public void test_aliased_instance_method() {
		int e1 = factory.culled(true).create();
		int e2 = factory.culled(false).create();
		
		assertEquals(true, cullible.get(e1).culled);
		assertEquals(false, cullible.get(e2).culled);
	}
	
	public void test_sticky_method() {
		int e1 = factory.hitPoints(100).create();
		int e2 = factory.create();
		
		assertEquals(100, hitpoints.get(e1).current);
		assertEquals(100, hitpoints.get(e2).current);
	}
	
	public void test_failing_sticky_method() {
		factory.hitPoints(100).create();
		try {
			factory.hitPoints(200).create();
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	public void test_sticky_copy() {
		factory.hitPoints(100).create();
		assertEquals(100, hitpoints.get(factory.copy().create()).current);
		assertEquals(200, hitpoints.get(factory.copy().hitPoints(200).create()).current);
	}
	
	public void test_setter() {
		int e1 = factory.pos(1, 2).create();
		int e2 = factory.pos(3, 4).create();
		
		assertEquals(1, complex.get(e1).pos.x, ACC);
		assertEquals(2, complex.get(e1).pos.y, ACC);
		assertEquals(3, complex.get(e2).pos.x, ACC);
		assertEquals(4, complex.get(e2).pos.y, ACC);
	}
	
	public void test_aliased_setter() {
		int e1 = factory.hoho(1, 2).create();
		int e2 = factory.hoho(3, 4).create();
		
		assertEquals(1, complex.get(e1).vel.x, ACC);
		assertEquals(2, complex.get(e1).vel.y, ACC);
		assertEquals(3, complex.get(e2).vel.x, ACC);
		assertEquals(4, complex.get(e2).vel.y, ACC);
	}
}
