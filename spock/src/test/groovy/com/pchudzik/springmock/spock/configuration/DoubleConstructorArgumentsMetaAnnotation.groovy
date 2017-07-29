package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@AutowiredSpy
@SpockDouble(constructorArguments = DoubleConstructorArgumentsTest.FakeArgumentsProvider)
@interface DoubleConstructorArgumentsMetaAnnotation {

}