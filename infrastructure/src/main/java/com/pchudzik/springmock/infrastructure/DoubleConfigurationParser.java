package com.pchudzik.springmock.infrastructure;

import javax.annotation.Nullable;
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
	 *
	 * @param configuration configuration annotation
	 * @return proper doubleConfiguration object or null if configuration is missing
	 */
	T parseMockConfiguration(String doubleName, @Nullable A configuration);

	/**
	 * <p>Will parse annotations and build spy configuration object from it.</p>
	 *
	 * @param configuration configuration annotation
	 * @return proper doubleConfiguration object or null if configuration is missing
	 */
	T parseSpyConfiguration(String doubleName, @Nullable A configuration);
}
