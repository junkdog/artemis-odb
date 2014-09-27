package com.artemis;

import com.artemis.reference.Ship;


public class ParamArchTest {
	
	private static EntityFactory factory;
	public static void main(String[] args) {
		World world = new World();
		factory = world.setManager(new EntityFactory());
		world.initialize();
		
		Ship shipTemplate = new ArchetypeBuilder()
			.add(Position.class)
			.add(Velocity.class)
			.add(Asset.class)
			.add(Size.class)
			.add(HitPoints.class)
			.buildTemplate(world, Ship.class);
		
		shipTemplate
			.position(120, 200)
			.asset("ship2")
			.velocity(20, 0)
			.group("allies");
			
		
//		world.createEntity(shipTemplate.tag("player"));
		
		Entity player = factory.ship.group("human").create();
	}
	
	public static class EntityFactory extends Manager {
		public Ship ship;
		public Ship shipSlow;
		public Ship shipFast;

		@Override
		protected void initialize() {
			ship = new ArchetypeBuilder()
				.add(Position.class)
				.add(Velocity.class)
				.add(Asset.class)
				.add(Size.class)
				.add(HitPoints.class)
				.buildTemplate(world, Ship.class);
			
			ship.position(120, 200)
				.asset("ship2")
				.velocity(20, 0)
				.group("allies");
			
			// or more concise
			shipSlow = new ArchetypeBuilder()
				.add(Position.class)
				.add(Velocity.class)
				.add(Asset.class)
				.add(Size.class)
				.add(HitPoints.class)
				.buildTemplate(world, Ship.class)
					.position(120, 200)
					.asset("ship14")
					.velocity(5, 0);
			
			// alternatively infer additional components
			// from the ParameterizedArchetype
			shipFast = new ArchetypeBuilder()
				.buildTemplate(world, Ship.class)
					.position(120, 200)
					.asset("ship42")
					.velocity(230, 0);

		}
	}
	
	
	public static class Position extends Component {
		public float x, y;
	}
	
	public static class Size extends Component {}
	public static class Asset extends Component {}
	public static class HitPoints extends Component {}
	public static class Velocity extends Component {
		public float x, y;
	}
}
