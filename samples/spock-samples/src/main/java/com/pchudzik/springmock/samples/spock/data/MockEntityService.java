package com.pchudzik.springmock.samples.spock.data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class MockEntityService {
	private final MockEntityRepository repository;

	MockEntityService(MockEntityRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public MockEntity save(MockEntity entity) {
		return repository.save(entity);
	}
}
