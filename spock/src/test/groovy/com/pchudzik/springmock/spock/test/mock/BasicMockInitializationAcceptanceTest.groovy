package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class BasicMockInitializationAcceptanceTest extends Specification {
    @AutowiredMock
    AnyService anyService

    def "should inject mock"() {
        expect:
        true == new MockUtil().isMock(anyService)
    }

    def "should inject functional mock"() {
        given:
        anyService.hello() >> "hello"

        when:
        final result = anyService.hello();

        then:
        result == "hello"
    }
}
