package scot.ianmacdonald.cakemgr.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.rest.model.CakeRepository;

@SpringBootTest
class CakeManagerApplicationTest {
	
	@Autowired
	private CakeRepository cakeRepository;
	
	@Autowired
	private RestTemplate restTemplate;

	@Test
	void contextLoads() {
		
		assertThat(cakeRepository).isNotNull();
		assertThat(cakeRepository).isInstanceOf(JpaRepository.class);
		assertThat(restTemplate).isNotNull();
	}

}
