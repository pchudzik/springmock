package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class MockNameGenerationTest {
	@Autowired
	ApplicationContext applicationContext;

	@AutowiredMock
	AnyService anyService;

	@Test
	public void should_inject_not_named_mock() {
		assertNotNull(anyService);
	}

	@Test
	public void should_generate_mock_name_based_on_field_name() {
		//when
		final AnyService service = applicationContext.getBean("anyService", AnyService.class);

		//then
		assertSame(anyService, service);
	}
}
