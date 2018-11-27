package com.pchudzik.springmock.samples.springboot2.spock;

import org.springframework.stereotype.Service;

@Service
public class LogService {
    public void logCall(int input, int result) {
        System.out.println("called with " + input + " result " + result);
    }
}
