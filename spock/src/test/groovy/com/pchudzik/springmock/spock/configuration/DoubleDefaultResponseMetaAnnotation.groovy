package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@AutowiredMock
@SpockDouble(defaultResponse = DoubleDefaultResponseTest.NoResponse)
@interface DoubleDefaultResponseMetaAnnotation {
}
