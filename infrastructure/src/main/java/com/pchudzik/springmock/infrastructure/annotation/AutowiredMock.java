package com.pchudzik.springmock.infrastructure.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Annotation which notifies springmock infrastructure to process the field annotated with it
 * and create mock inside of spring context and latter inject into specification
 */
@Repeatable(AutowiredMocks.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface AutowiredMock {
	/**
	 * Optional name of the created bean. If empty mock name will be created from the field name.
	 */
	String name() default "";

	/**
	 * Optional list of aliases for created bean.
	 */
	String [] alias() default {};

	/**
	 * Optional class of the mock. If present
	 * @return
	 */
	Class<?> doubleClass() default Void.class;
}
