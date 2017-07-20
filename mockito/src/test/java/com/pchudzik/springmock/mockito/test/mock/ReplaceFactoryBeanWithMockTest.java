package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ReplaceFactoryBeanWithMockTest {
	@AutowiredMock
	FactoryBean<AnyService> anyServiceFactoryBean;

	@Test
	public void should_replace_factory_bean_with_mock() throws Exception {
		//given
		final AnyService mockService = Mockito.mock(AnyService.class);
		Mockito.when(mockService.hello()).thenReturn("mock");
		Mockito.when(anyServiceFactoryBean.getObject()).thenReturn(mockService);

		//when
		final AnyService anyService = anyServiceFactoryBean.getObject();

		//then
		Assert.assertSame(mockService, anyService);
		Assert.assertEquals("mock", anyService.hello());
	}

	@Configuration
	static class Config {
		@Bean
		FactoryBean<AnyService> anyServiceFactoryBean() {
			return new FactoryBean<AnyService>() {
				@Override
				public AnyService getObject() throws Exception {
					return () -> "not a mock";
				}

				@Override
				public Class<?> getObjectType() {
					return AnyService.class;
				}

				@Override
				public boolean isSingleton() {
					return false;
				}
			};
		}
	}
}
