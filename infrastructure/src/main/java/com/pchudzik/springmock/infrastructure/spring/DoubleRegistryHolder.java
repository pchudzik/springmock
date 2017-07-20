package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;

/**
 * Holder class used as doubleRegistry factory. This way all beans can access double registry and I'm not forced to register singleton directly.
 */
class DoubleRegistryHolder {
	public static final String BEAN_NAME = DoubleRegistry.BEAN_NAME + ".holder";
	public static final String REGISTRY_FACTORY_METHOD = "getDoubleRegistry";

	private final DoubleRegistry doubleRegistry;

	DoubleRegistryHolder(DoubleRegistry doubleRegistry) {
		this.doubleRegistry = doubleRegistry;
	}

	public DoubleRegistry getDoubleRegistry() {
		return doubleRegistry;
	}
}
