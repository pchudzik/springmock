package com.pchudzik.springmock.spock.test.acceptance.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.configuration.SpockDouble
import org.spockframework.mock.DefaultJavaLangObjectInteractions
import org.spockframework.mock.IDefaultResponse
import org.spockframework.mock.IMockInvocation
import org.spockframework.mock.MockUtil
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class BasicMockInitializationAcceptanceTest extends Specification {
    @AutowiredMock
    AnyService anyService

    @AutowiredMock
    @SpockDouble(defaultResponse = ReturnsHelloMockResponse)
    AnyService withDefaultResponse

    def "should inject mock"() {
        expect:
        new MockUtil().isMock(anyService)
    }

    def "should inject functional mock"() {
        given:
        anyService.hello() >> "hello"

        when:
        final result = anyService.hello();

        then:
        result == "hello"
    }

    def "should configure mock default response"() {
        expect:
        withDefaultResponse.hello() == ReturnsHelloMockResponse.DEFAULT_RESPONSE
    }

    private static class ReturnsHelloMockResponse implements IDefaultResponse {
        public static final String DEFAULT_RESPONSE = "hello mock!!1"

        @Override
        Object respond(IMockInvocation invocation) {
            final interaction = DefaultJavaLangObjectInteractions.INSTANCE.match(invocation);
            if (interaction != null) {
                return interaction.accept(invocation)
            }

            return DEFAULT_RESPONSE
        }
    }

    static abstract class AnyService {
        abstract String hello()
    }
}
