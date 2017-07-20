package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MultipleClassesSpyClassTest {
	@AutowiredSpy
	Service spy1;

	@AutowiredSpy
	Service spy2;

	@Autowired
	Service service;

	@Test
	public void should_distinguish_multiple_spy_instances() {
		Assert.assertFalse(Mockito.mockingDetails(service).isSpy());

		Assert.assertTrue(Mockito.mockingDetails(spy1).isSpy());
		Assert.assertTrue(Mockito.mockingDetails(spy2).isSpy());
		Assert.assertNotSame(spy1, spy2);
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			return new Service(new ServiceInteractionRecorder());
		}

		@Bean
		Service spy1() {
			return new Service(new ServiceInteractionRecorder());
		}

		@Bean
		Service spy2() {
			return new Service(new ServiceInteractionRecorder());
		}
	}
}
