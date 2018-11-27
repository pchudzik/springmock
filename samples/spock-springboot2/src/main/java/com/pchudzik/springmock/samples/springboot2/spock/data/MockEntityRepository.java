package com.pchudzik.springmock.samples.springboot2.spock.data;

import org.springframework.data.jpa.repository.JpaRepository;

interface MockEntityRepository extends JpaRepository<MockEntity, Long> {
}
