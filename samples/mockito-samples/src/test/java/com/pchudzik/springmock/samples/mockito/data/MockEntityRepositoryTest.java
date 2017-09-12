package com.pchudzik.springmock.samples.mockito.data;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.samples.mockito.DoNothingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutowiredMock(doubleClass = DoNothingService.class)
public class MockEntityRepositoryTest {
	@Autowired
	MockEntityRepository mockEntityRepository;

	@Test
	public void should_save_entity() {
		//given
		final MockEntity entity = new MockEntity();
		entity.setName("mocked");

		//when
		mockEntityRepository.save(entity);

		//then
		assertNotNull(entity.getId());
	}
}