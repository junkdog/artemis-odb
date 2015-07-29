package java.lang;

import java.lang.annotation.*;

/**
 * @author Daan van Yperen
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface SafeVarargs {
}
