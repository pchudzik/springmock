package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class CreatedMockShouldBeInjectable extends Specification {
    @AutowiredMock
    AnyService anyService

    @Autowired
    OtherService otherService

    def "should inject created mock when it is required"() {
        expect:
        anyService.is(otherService.anyService)
    }

    @Configuration
    static class Config {
        @Bean
        OtherService otherService(@Autowired AnyService anyService) {
            new OtherService(anyService)
        }
    }

    static class OtherService {
        private final AnyService anyService

        OtherService(AnyService anyService) {
            this.anyService = anyService
        }
    }
}
