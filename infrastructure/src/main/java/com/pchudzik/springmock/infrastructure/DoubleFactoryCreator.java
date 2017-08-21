package com.pchudzik.springmock.infrastructure;

public interface DoubleFactoryCreator {
	String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleFactoryCreator";
	String FACTORY_METHOD_NAME = "createDoubleFactory";

	DoubleFactory createDoubleFactory();
}
