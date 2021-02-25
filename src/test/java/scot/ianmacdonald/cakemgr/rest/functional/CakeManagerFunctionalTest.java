package scot.ianmacdonald.cakemgr.rest.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(OrderAnnotation.class)
public class CakeManagerFunctionalTest {

	private RestTemplate restTemplate = new RestTemplate();

	// http request body strings

	@Value("${postcake.json.request}")
	private String postCakeJsonRequest;;

	@Value("${postcake.malformed.json.request}")
	private String postCakeMalformedJsonRequest;

	// http response body strings
	
	@Value("${getcakes.expected.response}")
	private String expectedGetCakesResponse;

	@Value("${getcakes.again.expected.response}")
	private String expectedGetCakesAgainResponse;

	@Value("${postcake.expected.response}")
	private String expectedPostCakeResponse;

	@Value("${postcake.duplicate.expected.response}")
	private String expectedPostDuplicateCakeResponse ;
	
	@Value("${postcake.malformed.expected.response}")
	private String expectedPostCakeMalformedJsonResponse;
	
	@Test
	@Order(1)
	public void testGetCakes() {

		testJsonHalRequest(HttpMethod.GET, null, HttpStatus.OK, expectedGetCakesResponse);
	}

	@Test
	@Order(2)
	public void testPostCake() throws Exception {

		testJsonHalRequest(HttpMethod.POST, postCakeJsonRequest, HttpStatus.CREATED, expectedPostCakeResponse);
	}

	@Test
	@Order(3)
	public void testPostDuplicateCake() throws Exception {

		testJsonHalRequest(HttpMethod.POST, postCakeJsonRequest, HttpStatus.FORBIDDEN,
				expectedPostDuplicateCakeResponse);
	}

	@Test
	@Order(4)
	public void testGetCakesAgain() throws Exception {

		testJsonHalRequest(HttpMethod.GET, null, HttpStatus.OK, expectedGetCakesAgainResponse);
	}

	@Test
	@Order(5)
	public void testPostCakeMalformedJson() throws Exception {

		testJsonHalRequest(HttpMethod.POST, postCakeMalformedJsonRequest, HttpStatus.BAD_REQUEST,
				expectedPostCakeMalformedJsonResponse);
	}

	private void testJsonHalRequest(final HttpMethod httpMethod, final String jsonRequestBody,
			final HttpStatus expectedHttpStatus, final String expectedJsonHalResponse) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(jsonRequestBody, httpHeaders);

		ResponseEntity<String> response = null;
		HttpStatus actualHttpStatus = null;
		HttpHeaders actualHeaders = null;
		MediaType actualContentType = null;
		String actualJsonHalResponse = null;
		try {
			response = restTemplate.exchange("http://localhost:8081/cakes", httpMethod, entity, String.class);
			actualHttpStatus = response.getStatusCode();
			actualHeaders = response.getHeaders();
			actualJsonHalResponse = response.getBody();
		} catch (HttpClientErrorException ex) {
			actualHttpStatus = ex.getStatusCode();
			actualHeaders = ex.getResponseHeaders();
			actualJsonHalResponse = ex.getResponseBodyAsString();
		}
		actualContentType = actualHeaders.getContentType();

		assertEquals(actualHttpStatus, expectedHttpStatus);
		assertEquals(MediaTypes.HAL_JSON, actualContentType);

		assertEquals(expectedJsonHalResponse, actualJsonHalResponse);
	}

}
