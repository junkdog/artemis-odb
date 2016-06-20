package com.artemis.annotations;

import com.artemis.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AspectDescriptor {
	Class<? extends Component>[] all() default {};
	Class<? extends Component>[] one() default {};
	Class<? extends Component>[] exclude() default {};
}
