package com.pchudzik.springmock.samples.spock;

import org.springframework.stereotype.Service;

@Service
public class AddOneTranslator {
	public int addOne(int a) {
		return a + 1;
	}
}
