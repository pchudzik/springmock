package com.pchudzik.springmock.samples.spock.data;

import org.springframework.data.jpa.repository.JpaRepository;

interface MockEntityRepository extends JpaRepository<MockEntity, Long> {
}
