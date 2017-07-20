package com.pchudzik.springmock.samples.mockito;

import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;

@Service
public class LogService {
	public void logCall(int argument, int response) {
		System.out.println("Called with " + argument + " response is " + response);
	}
}
