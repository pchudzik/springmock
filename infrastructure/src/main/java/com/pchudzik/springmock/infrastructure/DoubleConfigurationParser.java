package com.pchudzik.springmock.infrastructure;

import java.lang.annotation.Annotation;

/**
 * <p>Interface responsible for parsing double configuration. As a result it can produce any type of configuration depending on current mocking library requirements.</p>
 * <p>Returned object <b>must</b> implement equals and hashCode methods. Otherwise it will be impossible to take advantage of spring context caching</p>
 *
 * @param <T> double configuration
 * @param <A> annotation type to parse
 */
public interface DoubleConfigurationParser<T, A extends Annotation> {

	/**
	 * <p>Will parse annotations and build mock configuration object from it.</p>
	 * <p>
	 * <p>When you are not yet ready to introduce mock configuration then it's ok to return null from this method which will mean that there is no configuration available and {@link DoubleFactory} should handle it from here.
	 *
	 * @param configuration configuration annotation
	 * @return proper doubleConfiguration object or null if configuration is missing
	 */
	T parseMockConfiguration(String doubleName, A configuration);

	T parseSpyConfiguration(String doubleName, A configuration);
}
