package com.pchudzik.springmock.samples.mockito.data;

import org.springframework.data.jpa.repository.JpaRepository;

interface MockEntityRepository extends JpaRepository<MockEntity, Long> {
}
