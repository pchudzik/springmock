package com.pchudzik.springmock.infrastructure;

import java.lang.reflect.Field;

/**
 * <p>Interface responsible for parsing double configuration. As a result it can produce any type of configuration depending on current mocking library requirements.</p>
 *
 * <p>Returned object <b>must</b> implement equals and hashCode methods. Otherwise it will be impossible to take advantage of spring context caching</p>
 * @param <T>
 */
public interface DoubleConfigurationParser <T> {

	/**
	 * <p>Will parse field annotations and build mock configuration object from it.</p>
	 *
	 * <p>When you are not yet ready to introduce mock configuration then it's ok to return null from this method which will mean that there is no configuration available and {@link DoubleFactory} should handle it from here.
	 *
	 * @param field annotated field
	 * @return proper doubleConfiguration object or null if configuration is missing
	 */
	T parseDoubleConfiguration(Field field);
}
