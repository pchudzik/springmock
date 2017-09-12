package com.pchudzik.springmock.samples.mockito.data;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.configuration.MockitoDouble;
import com.pchudzik.springmock.samples.mockito.DoNothingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutowiredMock(doubleClass = DoNothingService.class)
public class MockEntityServiceTest {
	@AutowiredMock
	@MockitoDouble(defaultAnswer = ReturnsFirstArgument.class)
	MockEntityRepository mockEntityRepository;

	@Autowired
	MockEntityService mockEntityService;

	@Test
	public void should_inject_mock() {
		//given
		final MockEntity entity = new MockEntity();

		//when
		mockEntityService.save(entity);

		//then:
		Mockito
				.verify(mockEntityRepository)
				.save(entity);
	}

	static class ReturnsFirstArgument implements Answer<MockEntity> {

		@Override
		public MockEntity answer(InvocationOnMock invocationOnMock) throws Throwable {
			return (MockEntity) invocationOnMock.getArguments()[0];
		}
	}
}