package com.pchudzik.springmock.infrastructure.annotation;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AutowiredMocks {
	AutowiredMock [] value();
}
