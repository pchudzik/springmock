package com.pchudzik.springmock.samples.spock.data

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.samples.spock.DoNothingService
import com.pchudzik.springmock.spock.configuration.SpockDouble
import org.spockframework.mock.IDefaultResponse
import org.spockframework.mock.IMockInvocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
@AutowiredMock(doubleClass = DoNothingService)
class MockEntityServiceTest extends Specification {
	@AutowiredMock
	@SpockDouble(defaultResponse = ReturnFirstArgumentResponse)
	MockEntityRepository mockEntityRepository

	@Autowired
	MockEntityService mockEntityService

	def "should inject mock"() {
		given:
		final entity = new MockEntity()

		when:
		mockEntityService.save(entity)

		then:
		1 * mockEntityRepository.save(entity)
	}

	static class ReturnFirstArgumentResponse implements IDefaultResponse {
		@Override
		Object respond(IMockInvocation invocation) {
			invocation.arguments[0]
		}
	}
}
