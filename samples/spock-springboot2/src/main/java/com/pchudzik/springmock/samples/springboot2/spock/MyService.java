package com.pchudzik.springmock.samples.springboot2.spock;

import org.springframework.stereotype.Service;

@Service
public class MyService {
    private final LogService logService;
    private final DoNothingService doNothingService;
    private final TwoRepository twoRepository;
    private final AddOneTranslator addOneTranslator;

    public MyService(LogService logService, DoNothingService doNothingService, TwoRepository twoRepository, AddOneTranslator addOneTranslator) {
        this.logService = logService;
        this.doNothingService = doNothingService;
        this.twoRepository = twoRepository;
        this.addOneTranslator = addOneTranslator;
    }

    public int calculate(int a) {
        final int result = addOneTranslator.addOne(a) + twoRepository.getTwo();
        logService.logCall(a, result);
        doNothingService.noop();
        return result;
    }
}
