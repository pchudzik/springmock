package com.pchudzik.springmock.samples.springboot2.spock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.configuration.SpockDouble
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification

import static com.pchudzik.springmock.samples.springboot2.spock.TwoRepository.TWO

@SpringBootTest(classes = Springboot2Application)
@AutowiredMock(doubleClass = DoNothingService.class)
class SpockSamplesApplicationTest extends Specification {
	@AutowiredMock
	@SpockDouble(stub = true)
	AddOneTranslator addOneTranslator

	@AutowiredSpy
	TwoRepository twoRepository

	@MockBean
	LogService logService

	@Autowired
	MyService myService

	def "should calculate values"() {
		given:
		final inputA = 1
		final translatedA = 10
		final expectedResult = translatedA + TWO
		addOneTranslator.addOne(inputA) >> translatedA

		when:
		final result = myService.calculate(inputA)

		then:
		1 * twoRepository.getTwo()
		result == expectedResult
		Mockito.verify(logService).logCall(inputA, expectedResult)
	}
}
