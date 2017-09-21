# springmock

[![Build Status](https://travis-ci.org/pchudzik/springmock.svg?branch=master)](https://travis-ci.org/pchudzik/springmock)
[![Coverage Status](https://coveralls.io/repos/github/pchudzik/springmock/badge.svg?branch=badges)](https://coveralls.io/github/pchudzik/springmock?branch=badges)

## Introduction

Alternative spring mocking infrastructure. With pluggable mocking library support. The purpose is to
allow you to easily inject mocks created by any mocking library into your spring tests. Currently,
mockito and spock mocks are supported.

Why? Spring boot supports only mockito as mocks provider which is great if you write tests in java.
When using spock you might want to use mocks created by spock because of syntactic sugar they offer. 
It is similar to @MockBean any @SpyBean from spring-boot-test, but allows you to use mocks created 
by a library of your choice.

## Contents

* [Introduction](#introduction)
* [Contents](#contents)
* [Features](#features)
* [Requirements](#requirements)
  * [Mockito](#mockito)
  * [Spock](#spock)
* [Installation](#installation)
  * [Releases](#releases)
    * [With Mockito as mocks provider](#with-mockito-as-mocks-provider)
      * [mvn](#mvn)
      * [gradle](#gradle)
    * [With Spock as mock provider](#with-spock-as-mock-provider)
      * [mvn](#mvn-1)
      * [gradle](#gradle-1)
  * [Snapshots](#snapshots)
    * [Repository configuration](#repository-configuration)
    * [Mockito as mocks provider](#mockito-as-mocks-provider)
    * [Spock as mocks provider](#spock-as-mocks-provider)
* [Usage](#usage)
  * [Mocks](#mocks)
  * [Spies](#spies)
  * [Configuration](#configuration)
    * [mockito](#mockito-1)
    * [spock](#spock-1)
* [Problems](#problems)
* [Changelog](#changelog)
  * [1.1.2 - springmock-mockito - 2017.09.17](#112-springmock-mockito---20170917)
  * [1.1.2 - springmock-spock - 2017.09.17](#112-springmock-spock---20170917)
  * [1.1.0 - springmock-mockito - 2017.09.06](#110-springmock-mockito---20170906)
  * [1.1.0 - springmock-spock - 2017.09.06](#110-springmock-spock---20170906)
  * [1.0.0 - 2017.07.22](#100---20170722)

## Features

* inject mocks created by spock/mockito into test case ([spock
  samples](spock/src/test/groovy/com/pchudzik/springmock/spock/test), [mockito
  samples](mockito/src/test/java/com/pchudzik/springmock/mockito/test))
* extended mocks and spies configuration ([spock configuration
  options](spock/src/main/java/com/pchudzik/springmock/spock/configuration/SpockDouble.java),
  [mockito configuration
  options](mockito/src/main/java/com/pchudzik/springmock/mockito/configuration/MockitoDouble.java))
* registration of mocks declared on configuration/test class in spring context (just place @AutowiredMock @AutowiredSpy on class)
* partial mocks ([spock
  sample](spock/src/test/groovy/com/pchudzik/springmock/spock/test/spy/SpyShouldBeCreatedWithoutExistingObjectInstanceTest.groovy),
  [mockito
  sample](mockito/src/test/java/com/pchudzik/springmock/mockito/test/spy/SpyShouldBeCreatedWithoutExistingObjectInstanceTest.java))

## Requirements

Basic requirements to make it running:

* java8
* spring <= 4.3.6
* springboot <= 1.4.4

Required spring modules: spring-context & spring-test

### Mockito

mockito-core <= 1.10.x is required and must be provided. [Samples with spring-boot](samples/mockito-samples)

### Spock

To get spock mocks running you'll need:

* spock-core 1.1-groovy-2.4
* spock-spring 1.1-groovy-2.4

[Samples with spring-boot](samples/spock-samples)

## Installation

### Releases

#### With Mockito as mocks provider

##### mvn

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.pchudzik.springmock/springmock-mockito/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.pchudzik.springmock/springmock-mockito)

```xml
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-mockito</artifactId>
  <version>1.1.2</version>
</dependency>
```

##### gradle

```java
testCompile('com.pchudzik.springmock:springmock-mockito:1.1.2')
``` 

#### With Spock as mock provider 

##### mvn

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.pchudzik.springmock/springmock-spock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.pchudzik.springmock/springmock-spock)

```xml
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-spock</artifactId>
  <version>1.1.2</version>
</dependency>
```

##### gradle

```
testCompile('com.pchudzik.springmock:springmock-spock:1.1.2')
```

[sample pom.xml with spring-boot](samples/mockito-samples/pom.xml)

[sample build.gradle](samples/spock-samples/build.gradle)

### Snapshots

#### Repository configuration

Add [sonatype snapshots](https://oss.sonatype.org/content/repositories/snapshots) repository to
repositories list

Maven:
```
<repositories>
  <repository>
    <id>sonatype-snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

Gradle:
```
repositories {
  maven {
    url 'https://oss.sonatype.org/content/repositories/snapshots'
  }
  mavenCentral()
}
```

#### Mockito as mocks provider

Include mvn dependency:
```
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-mockito</artifactId>
  <version>1.2.0-SNAPSHOT</version>
</dependency>
```

Or gradle dependency:

```
testCompile('com.pchudzik.springmock:springmock-mockito:1.2.0-SNAPSHOT')
```

#### Spock as mocks provider

Include mvn dependency:
```
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-spock</artifactId>
  <version>1.2.0-SNAPSHOT</version>
</dependency>
```

Or gradle dependency:

```
testCompile('com.pchudzik.springmock:springmock-spock:1.2.0-SNAPSHOT')
```

## Usage

Mock injection infrastructure is the same and is independent to selected mock provider.

Once mock is injected you can use it accordingly to selected mocking library [for
mockito](samples/mockito-samples/src/test/java/com/pchudzik/springmock/samples/mockito/MockitoSamplesApplicationTests.java)
and [for
spock](samples/spock-samples/src/test/groovy/com/pchudzik/springmock/samples/spock/SpockSamplesApplicationTest.groovy)

### Mocks

To inject mocks just annotate field you want to be initialized and injected with mock using @AutowiredMock and you are
good to go

```java
@AutowiredMock AnyService anyService;

@Test
public void should_inject_mock() {
  assertTrue(mockingDetails(anyService).isMock());
}
```

or in spock:
```
@AutowiredMock AService service

def "should inject mock"() {
  given: service.hello() >> "mock"
  when:  final result = service.hello()
  then:  result == "mock"
}
```

You can specify name of the registered mock using name param. Which will result in registering mock with the specific
name in the spring context (like in with
[@Bean#name](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html#name--)).
If name is not provided bean name will be the same ase the field name on which it is declared.

You can also provide list of aliases for mock using alias attribute.

[Sample test case with mockito mocks](samples/mockito-samples/src/test/java/com/pchudzik/springmock/samples/mockito/MockitoSamplesApplicationTests.java)

[Sample test case with spock mocks](samples/spock-samples/src/test/groovy/com/pchudzik/springmock/samples/spock/SpockSamplesApplicationTest.groovy)

### Spies

Right now springmock can not create spies on the fly and inject them in the context. In order to spy on the bean it must
already be registered in the context. It needs appropriate object to already exist in spring context.

```java
@AutowiredSpy Service service;

@Test
public void should_inject_spy() {
  assertTrue(mockingDetails(service).isSpy());
}
```

or in spock:

```
@AutowiredSpy Service service

def "should inject spy"() {
  when: service.hello()
  then: 1 * service.hello() >> "spy!"
}
```

You can specify name of the bean which should be replaced by created spy using name attribute. if no name is defined
then destination bean will be matched field name or by class.

### Configuration

#### Mockito

Annotate @AutowiredMock or @AutowiredSpy field with
[@MockitoDouble](mockito/src/main/java/com/pchudzik/springmock/mockito/configuration/MockitoDouble.java). Examples:

* [ShouldParseDoubleResetModeTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldParseDoubleResetModeTest.java)
* [ShouldConfigureAnswerTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldConfigureAnswerTest.java)
* [ShouldAssignExtraInterfacesToCreatedMockTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldAssignExtraInterfacesToCreatedMockTest.java)
* [ShouldConfigureMockSerializableModeTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldConfigureMockSerializableModeTest.java)
* [ShouldConfigureVerboseDoubleTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldConfigureVerboseDoubleTest.java)
* [ShouldConfigureStubOnlyDoubleTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldConfigureStubOnlyDoubleTest.java)
* [ShouldConfigureInvocationListenerTest](mockito/src/test/java/com/pchudzik/springmock/mockito/configuration/ShouldConfigureInvocationListenerTest.java)

#### Spock

Annotate @AutowiredMock or @AutowiredSpy field with
[@SpockDouble](spock/src/main/java/com/pchudzik/springmock/spock/configuration/SpockDouble.java). Examples:

* [DoubleConstructorArgumentsTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/DoubleConstructorArgumentsTest.groovy)
* [DoubleDefaultResponseTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/DoubleDefaultResponseTest.groovy)
* [GlobalSpockDoubleTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/GlobalSpockDoubleTest.groovy)
* [SpockDoubleImplementationTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/SpockDoubleImplementationTest.groovy)
* [StubDoublesTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/StubDoublesTest.groovy)
* [UseObjenesisInDoublesTest](spock/src/test/groovy/com/pchudzik/springmock/spock/configuration/UseObjenesisInDoublesTest.groovy)

## Problems

Please report any problems with the library using Github issues. I'd really appreciate failing test case included in
issue description or as PR.

## Changelog

### 1.2.0 springmock-spock - to be released

  * register field name as double alias - [#9](https://github.com/pchudzik/springmock/issues/9)
  
### 1.2.0 springmock-mockito - to be released

  * register field name as double alias - [#9](https://github.com/pchudzik/springmock/issues/9)

### 1.1.2 springmock-spock - 2017.09.17
  
  * Fixed spy reset code to handle missing beans - [#15](https://github.com/pchudzik/springmock/issues/15)
  * Single bean instance will be replaced by mock/spy without matching name [#10](https://github.com/pchudzik/springmock/issues/10)
  
### 1.1.2 springmock-mockito - 2017.09.17
  
  * Single bean instance will be replaced by mock/spy without matching name [#10](https://github.com/pchudzik/springmock/issues/10) 

### 1.1.0 springmock-mockito - 2017.09.06
  
  * mocks configuration - [@MockitoDouble](mockito/src/main/java/com/pchudzik/springmock/mockito/configuration/MockitoDouble.java)
  * fixed mocks reset in context hierarchy
  * optimizations to listener responsible for mock reset between tests methods executions
  * class level mocks 
    [sample](infrastructure/src/test/java/com/pchudzik/springmock/infrastructure/test/TestCaseClassLevelDoublesShouldBeRegisteredTest.java)
  * mocks and spies can be created and injected into @Configuration classes
    ([mock injection example](mockito/src/test/java/com/pchudzik/springmock/mockito/test/mock/ShouldInjectMocksInConfigurationClass.java),
    [spy injection example](mockito/src/test/java/com/pchudzik/springmock/mockito/test/spy/ShouldInjectSpiesInConfigurationClass.java))
  * added possibility to create spies without real object present in context.
    Word of warning. It will produce partial mock, which might not work as you'd expect it to work and might cause some
    unexpected failures. In general yous should spy on existing bean instance, unless it is very trivial bean without 
    any fields. 

### 1.1.0 springmock-spock - 2017.09.06

  * mocks configuration - [@SpockDouble](spock/src/main/java/com/pchudzik/springmock/spock/configuration/SpockDouble.java)
  * optimizations to listener attaching mocks to the currently running specification
  * should not fail when executing on mixed code base with spock and junit tests - [#11](https://github.com/pchudzik/springmock/issues/11) 
  * class level mocks 
    [sample](infrastructure/src/test/java/com/pchudzik/springmock/infrastructure/test/TestCaseClassLevelDoublesShouldBeRegisteredTest.java)
  * mocks and spies can be created and injected into @Configuration classes
    ([mock injection example](spock/src/test/groovy/com/pchudzik/springmock/spock/test/mock/ShouldInjectMocksInConfigurationClass.groovy),
    [spy injection example](spock/src/test/groovy/com/pchudzik/springmock/spock/test/spy/ShouldInjectSpiesInConfigurationClass.groovy)) 
  * added possibility to create spies without real object present in context.
    Word of warning as in mockito it might produce not properly initialized object. In spock you have higher level of
    control over mocks creation and you can initialize object properly using
    [SpockDouble.constructorArguments](spock/src/main/java/com/pchudzik/springmock/spock/configuration/SpockDouble.java)
    see [integration test
    case](spock/src/test/groovy/com/pchudzik/springmock/spock/test/spy/SpyShouldBeCreatedWithoutExistingObjectInstanceTest.groovy).

### 1.0.0 - 2017.07.22

  * @AutowiredMock with name and aliases support
  * @AutowiredSpy with name and aliases support
