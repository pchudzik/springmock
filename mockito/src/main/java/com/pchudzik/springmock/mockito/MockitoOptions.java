package com.pchudzik.springmock.mockito;

public class MockitoOptions {


	public static class ResetMode {
		public static final String RESET_MODE = MockitoConstants.PACKAGE_PREFIX + "RESET_MODE";

		public static final String NONE = RESET_MODE + ".NONE";
		public static final String BEFORE = RESET_MODE + ".BEFORE";
		public static final String AFTER = RESET_MODE + ".AFTER";
	}
}
