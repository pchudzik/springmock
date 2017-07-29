package com.pchudzik.springmock.infrastructure.annotation;


import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Annotation which notifies springmock infrastructure to process the field annotated with it
 * and locate bean matching field and annotation definition and create spy from it.
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface AutowiredSpy {
	/**
	 * Optional name of the bean which should be located and replaced with spy.
	 */
	String name() default "";

	/**
	 * Optional list of aliases for to match spy definition to spring bean.
	 */
	String [] alias() default {};
}
