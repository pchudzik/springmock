package com.pchudzik.springmock.samples.mockito;

import org.springframework.stereotype.Service;

@Service
public class MyService {
	private final LogService logService;
	private final TwoRepository twoRepository;
	private final AddOneTranslator addOneTranslator;

	public MyService(LogService logService, TwoRepository twoRepository, AddOneTranslator addOneTranslator) {
		this.logService = logService;
		this.twoRepository = twoRepository;
		this.addOneTranslator = addOneTranslator;
	}

	public int calculate(int a) {
		final int result = addOneTranslator.addOne(a) + twoRepository.getTwo();
		logService.logCall(a, result);
		return result;
	}
}
