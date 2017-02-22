package sk.solver.common.genericparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generic Parser post process class
 * If class have this annotation, on autogeneration process there will be postProcess abstract method called on every object
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GPPostProcess {
}
