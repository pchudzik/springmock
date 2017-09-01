package com.pchudzik.springmock.infrastructure.test.inject;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(ShouldReplaceFactoryBeanWithDoubles.TestContextBootstrapper.class)
public class ShouldReplaceFactoryBeanWithDoubles {
	private static final FactoryBean aMock = Mockito.mock(FactoryBean.class);
	private static final FactoryBean aSpy = Mockito.mock(FactoryBean.class);

	private static final String MOCK = "mock";
	private static final String SPY = "spy";

	@AutowiredMock(name = MOCK)
	@Qualifier(MOCK)
	FactoryBean<Service> mockFactory;

	@AutowiredSpy(name = SPY)
	@Qualifier(SPY)
	FactoryBean<Service> spyFactory;

	@Test
	public void should_replace_factory_bean_with_mock() {
		assertSame(aMock, mockFactory);
	}

	@Test
	public void should_replace_factory_bean_with_spy() {
		assertSame(aSpy, spyFactory);
	}

	@Configuration
	static class Config {
		static final Service notMock = new Service();
		static final Service notSpy = new Service();

		@Bean(MOCK)
		FactoryBean<Service> notMockFactoryBean() {
			return new FactoryBean<Service>() {
				@Override
				public Service getObject() throws Exception {
					return notMock;
				}

				@Override
				public Class<?> getObjectType() {
					return Service.class;
				}

				@Override
				public boolean isSingleton() {
					return true;
				}
			};
		}

		@Bean(SPY)
		FactoryBean<Service> notSpyFactoryBean() {
			return new FactoryBean<Service>() {
				@Override
				public Service getObject() throws Exception {
					return notSpy;
				}

				@Override
				public Class<?> getObjectType() {
					return Service.class;
				}

				@Override
				public boolean isSingleton() {
					return true;
				}
			};
		}
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock, byNameMockMatcher(MOCK))
					.spy(aSpy, byNameSpyMatcher(SPY))
					.build());
		}
	}

	static class Service {
	}
}
