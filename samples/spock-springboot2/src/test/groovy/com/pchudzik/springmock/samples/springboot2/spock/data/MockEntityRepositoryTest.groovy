package com.pchudzik.springmock.samples.springboot2.spock.data

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.samples.springboot2.spock.DoNothingService
import com.pchudzik.springmock.samples.springboot2.spock.Springboot2Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringBootTest(classes = Springboot2Application)
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
