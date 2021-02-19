package scot.ianmacdonald.cakemgr.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.rest.model.Cake;
import scot.ianmacdonald.cakemgr.rest.model.CakeRepository;

@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {

		RestTemplate restTemplate = builder.build();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter
				.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		return restTemplate;
	}

	@Bean
	List<Cake> initialCakeList(RestTemplate restTemplate) {

		ResponseEntity<Cake[]> cakesResponseBody = restTemplate.getForEntity(
				"https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json",
				Cake[].class);
		Cake[] cakeArray = cakesResponseBody.getBody();
		List<Cake> uniqueCakeList = Arrays.stream(cakeArray).distinct().collect(Collectors.toList());
		return uniqueCakeList;
	}

	@Bean
	CommandLineRunner initDatabase(CakeRepository cakeRepository, List<Cake> cakeList) {
		
		// TODO: remove... corrupt canonical data to test flow of testing in dev and CI
		cakeList.add(new Cake("Chocolate Cake", "Its so chok letty it hurts", "http://www.allthechoklet.com"));

		return args -> {
			cakeList.stream().peek(c -> log.info("Preloading " + c.toString() + " to Database"))
					.forEach(c -> cakeRepository.save(c));
		};
	}

}