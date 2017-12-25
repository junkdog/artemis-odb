package com.artemis.utils.reflect;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.One;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class SystemMetadataTest {

    @Test
    public void When_system_with_no_annotations_Should_not_explode() {
        abstract class Test extends BaseSystem {
        }
        assertNull(new SystemMetadata(Test.class).getAspect());
    }

    @Test
    public void When_all_annotation_Should_return_matching_aspect() {
        @All(ComponentX.class)
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.all(ComponentX.class), new SystemMetadata(Test.class).getAspect());
    }

    @Test
    public void When_all_annotation_with_two_classes_Should_return_matching_aspect() {
        @All({ComponentX.class, ComponentY.class})
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.all(ComponentX.class, ComponentY.class), new SystemMetadata(Test.class).getAspect());
    }


    @Test
    public void When_any_annotation_Should_return_matching_aspect() {
        @One(ComponentX.class)
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.one(ComponentX.class), new SystemMetadata(Test.class).getAspect());
    }

    @Test
    public void When_any_annotation_with_two_classes_Should_return_matching_aspect() {
        @One({ComponentX.class, ComponentY.class})
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.one(ComponentX.class, ComponentY.class), new SystemMetadata(Test.class).getAspect());
    }


    @Test
    public void When_exclude_annotation_Should_return_matching_aspect() {
        @Exclude(ComponentX.class)
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.exclude(ComponentX.class), new SystemMetadata(Test.class).getAspect());
    }

    @Test
    public void When_exclude_annotation_with_two_classes_Should_return_matching_aspect() {
        @Exclude({ComponentX.class, ComponentY.class})
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.exclude(ComponentX.class, ComponentY.class), new SystemMetadata(Test.class).getAspect());
    }


    @Test
    public void When_composite_annotations_Should_return_matching_aspect() {
        class EX extends Component {
        }
        class ONE extends Component {
        }
        class TWO extends Component {
        }
        class ALL extends Component {
        }
        @Exclude({EX.class})
        @One({ONE.class, TWO.class})
        @All({ALL.class})
        abstract class Test extends BaseSystem {
        }
        assertEquals(Aspect.exclude(EX.class).one(ONE.class, TWO.class).all(ALL.class), new SystemMetadata(Test.class).getAspect());
    }
}