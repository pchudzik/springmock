package com.pchudzik.springmock.mockito.experimental;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;

class ClassLevelMocks {
	@AutowiredMock(doubleClass = Object.class, name = "xxx", alias = {"x1", "x2"})
	static class SimpleCaseConfig {}
}
