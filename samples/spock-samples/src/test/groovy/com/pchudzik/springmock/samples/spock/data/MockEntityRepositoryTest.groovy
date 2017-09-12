package com.pchudzik.springmock.samples.spock.data

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.samples.spock.DoNothingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringBootTest
@Transactional
@AutowiredMock(doubleClass = DoNothingService)
class MockEntityRepositoryTest extends Specification {
	@Autowired
	MockEntityRepository mockEntityRepository

	def "should save entity"() {
		given:
		final entity = new MockEntity()
		entity.name = "mocked"

		when:
		mockEntityRepository.save(entity)

		then:
		entity.id != null
	}
}
