package com.pchudzik.springmock.samples.mockito;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockitoSamplesApplicationTests {
	@AutowiredMock
	AddOneTranslator addOneTranslator;

	@AutowiredSpy
	TwoRepository twoRepository;

	@Autowired
	MyService myService;

	@MockBean
	LogService logService;

	@Test
	public void should_calculate_values() {
		//given
		final int inputA = 1;
		final int translatedA = 10;
		final int expectedResult = translatedA + TwoRepository.TWO;
		Mockito
				.when(addOneTranslator.addOne(inputA))
				.thenReturn(translatedA);

		//when
		final int result = myService.calculate(inputA);

		//then
		Assert.assertEquals(expectedResult, result);
		Mockito.verify(twoRepository).getTwo();
		Mockito.verify(logService).logCall(inputA, expectedResult);
	}
}
