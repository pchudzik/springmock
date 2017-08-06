package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class SpyShouldBeCreatedWithoutExistingObjectInstanceTest {
	@AutowiredSpy
	Service service;

	@Test
	@Ignore("Still in progress")
	public void should_inject_spy_when_no_backing_object_present() {
		assertTrue(mockingDetails(service).isSpy());
	}
}
