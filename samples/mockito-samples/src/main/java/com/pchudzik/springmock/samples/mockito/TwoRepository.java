package com.pchudzik.springmock.samples.mockito;

import org.springframework.stereotype.Repository;

@Repository
public class TwoRepository {
	public static final int TWO = 2;

	public int getTwo() {
		return TWO;
	}
}
