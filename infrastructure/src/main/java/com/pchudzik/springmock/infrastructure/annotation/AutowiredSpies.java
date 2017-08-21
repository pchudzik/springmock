package com.pchudzik.springmock.infrastructure.annotation;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutowiredSpies {
	AutowiredSpy [] value();
}
