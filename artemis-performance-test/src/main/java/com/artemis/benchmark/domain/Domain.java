package com.artemis.benchmark.domain;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;

/**
 * Date: 29/7/2015
 * Time: 20:04 PM
 *
 * @author Snorre E. Brekke
 */
public class Domain{
    public static class Position extends Component {

    }

    public static class Velocity extends Component{

    }


    public static class Health extends Component{

    }


    public static class Damage extends Component{

    }

    public static class Size extends Component{

    }

    public static class Color extends Component{

    }

    @Wire
    public static class CustomWired{
        private PositionSystem positionSystem;
        private TagManager tagManager;
        private ComponentMapper<Velocity> cm;
        private String undrelated;
        @Wire(name = "string")
        private String injectedString;
        @Wire
        private Object injectedObject;

    }

    @Wire
    public static class PositionSystem extends EntitySystem {
        protected ComponentMapper<Position> pm;

        public PositionSystem() {
            super(Aspect.all(Position.class));
        }

        @Override
        protected void processSystem() {

        }
    }

    @Wire
         public static class VelocitySystem extends EntitySystem{
        protected ComponentMapper<Position> pm;
        protected ComponentMapper<Velocity> vm;

        public VelocitySystem() {
            super(Aspect.all(Position.class, Velocity.class));
        }

        @Override
        protected void processSystem() {

        }
    }

    public static class DamageSystem extends EntitySystem{
        @Mapper
        protected ComponentMapper<Health> pm;
        @Mapper
        protected ComponentMapper<Damage> vm;

        public DamageSystem() {
            super(Aspect.one(Health.class, Damage.class));
        }

        @Override
        protected void processSystem() {

        }
    }


    @Wire
    public static class ComplexSystem extends EntitySystem{
        protected ComponentMapper<Position> pm;
        protected ComponentMapper<Color> cm;
        protected ComponentMapper<Size> sm;
        protected AspectSubscriptionManager asm;

        public ComplexSystem() {
            super(Aspect.all(Position.class, Color.class, Size.class));
        }

        @Override
        protected void processSystem() {

        }
    }
}
