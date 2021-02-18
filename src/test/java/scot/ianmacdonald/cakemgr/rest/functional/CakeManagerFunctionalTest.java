package scot.ianmacdonald.cakemgr.rest.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Class to make functional integration test of running RESTful service.
 * 
 * @TestMethodOrder(OrderAnnotation.class) annotation is used to mandate the
 *                                         order the tests run in so the DB does
 *                                         not have to be torn down and set up
 *                                         in between tests. This is not best
 *                                         practice as ideally each test runs in
 *                                         an idempotent fashion. However, the
 *                                         tests will be launched from maven
 *                                         which will handle starting and
 *                                         stopping the application so the
 *                                         results are predictable.
 * 
 * @author ian.macdonald@ianmacdonald.scot
 */
@TestMethodOrder(OrderAnnotation.class)
public class CakeManagerFunctionalTest {

	// test data

	private RestTemplate restTemplate = new RestTemplate();

	// http request Strings

	private final String putCakeJsonRequest = "{\n" + "  \"title\" : \"Rees Krispy Kreme Donut\",\n"
			+ "  \"desc\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "  \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\"\n"
			+ "}";

	private final String putCakeMalformedJsonRequest = "{\n" + "  :title\" : \"Rees Krispy Kreme Donut\",\n"
			+ "  \"desc\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "  \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\"\n"
			+ "}";

	// http response strings

	private final String expectedGetCakesResponse = "{\n" + "  \"_embedded\" : {\n" + "    \"cakes\" : [ {\n"
			+ "      \"title\" : \"Lemon cheesecake\",\n" + "      \"description\" : \"A cheesecake made of lemon\",\n"
			+ "      \"image\" : \"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/1\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/1\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"victoria sponge\",\n" + "      \"description\" : \"sponge with jam\",\n"
			+ "      \"image\" : \"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/2\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/2\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Carrot cake\",\n" + "      \"description\" : \"Bugs bunnys favourite\",\n"
			+ "      \"image\" : \"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/3\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/3\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Banana cake\",\n" + "      \"description\" : \"Donkey kongs favourite\",\n"
			+ "      \"image\" : \"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/4\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/4\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Birthday cake\",\n" + "      \"description\" : \"a yearly treat\",\n"
			+ "      \"image\" : \"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/5\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/5\"\n" + "        }\n" + "      }\n" + "    } ]\n"
			+ "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n"
			+ "      \"href\" : \"http://localhost:8081/cakes\"\n" + "    },\n" + "    \"profile\" : {\n"
			+ "      \"href\" : \"http://localhost:8081/profile/cakes\"\n" + "    }\n" + "  },\n" + "  \"page\" : {\n"
			+ "    \"size\" : 20,\n" + "    \"totalElements\" : 5,\n" + "    \"totalPages\" : 1,\n"
			+ "    \"number\" : 0\n" + "  }\n" + "}";

	private final String expectedGetCakesAgainResponse = "{\n" + "  \"_embedded\" : {\n" + "    \"cakes\" : [ {\n"
			+ "      \"title\" : \"Lemon cheesecake\",\n" + "      \"description\" : \"A cheesecake made of lemon\",\n"
			+ "      \"image\" : \"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/1\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/1\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"victoria sponge\",\n" + "      \"description\" : \"sponge with jam\",\n"
			+ "      \"image\" : \"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/2\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/2\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Carrot cake\",\n" + "      \"description\" : \"Bugs bunnys favourite\",\n"
			+ "      \"image\" : \"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/3\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/3\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Banana cake\",\n" + "      \"description\" : \"Donkey kongs favourite\",\n"
			+ "      \"image\" : \"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/4\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/4\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Birthday cake\",\n" + "      \"description\" : \"a yearly treat\",\n"
			+ "      \"image\" : \"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/5\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/5\"\n" + "        }\n" + "      }\n" + "    }, {\n"
			+ "      \"title\" : \"Rees Krispy Kreme Donut\",\n"
			+ "      \"description\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "      \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\",\n"
			+ "      \"_links\" : {\n" + "        \"self\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/6\"\n" + "        },\n" + "        \"cake\" : {\n"
			+ "          \"href\" : \"http://localhost:8081/cakes/6\"\n" + "        }\n" + "      }\n" + "    } ]\n"
			+ "  },\n" + "  \"_links\" : {\n" + "    \"self\" : {\n"
			+ "      \"href\" : \"http://localhost:8081/cakes\"\n" + "    },\n" + "    \"profile\" : {\n"
			+ "      \"href\" : \"http://localhost:8081/profile/cakes\"\n" + "    }\n" + "  },\n" + "  \"page\" : {\n"
			+ "    \"size\" : 20,\n" + "    \"totalElements\" : 6,\n" + "    \"totalPages\" : 1,\n"
			+ "    \"number\" : 0\n" + "  }\n" + "}";

	private final String expectedPostCakeResponse = "{\n" + "  \"title\" : \"Rees Krispy Kreme Donut\",\n"
			+ "  \"description\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "  \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\",\n"
			+ "  \"_links\" : {\n" + "    \"self\" : {\n" + "      \"href\" : \"http://localhost:8081/cakes/6\"\n"
			+ "    },\n" + "    \"cake\" : {\n" + "      \"href\" : \"http://localhost:8081/cakes/6\"\n" + "    }\n"
			+ "  }\n" + "}";

	private final String expectedPostDuplicateCakeResponse = "{\"status\":\"FORBIDDEN\",\"message\":\"It is forbidden to create a Cake with a duplicate title\",\"debugMessage\":\"could not execute statement; SQL [n/a]; constraint [\\\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 6\\\"; SQL statement:\\ninsert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\"}";

	private final String expectedPostCakeMalformedJsonResponse = "{\"status\":\"BAD_REQUEST\",\"message\":\"The JSON message in the HTTP request was malformed\",\"debugMessage\":\"JSON parse error: Unexpected character (':' (code 58)): was expecting double-quote to start field name; nested exception is com.fasterxml.jackson.core.JsonParseException: Unexpected character (':' (code 58)): was expecting double-quote to start field name\\n at [Source: (org.apache.catalina.connector.CoyoteInputStream); line: 2, column: 4]\"}";

	@Test
	@Order(1)
	public void testGetCakes() {

		testJsonHalRequest(HttpMethod.GET, null, HttpStatus.OK, expectedGetCakesResponse);
	}

	@Test
	@Order(2)
	public void testPostCake() throws Exception {

		testJsonHalRequest(HttpMethod.POST, putCakeJsonRequest, HttpStatus.CREATED, expectedPostCakeResponse);

	}

	@Test
	@Order(3)
	public void testPostDuplicateCake() throws Exception {

		testJsonHalRequest(HttpMethod.POST, putCakeJsonRequest, HttpStatus.FORBIDDEN,
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

		testJsonHalRequest(HttpMethod.POST, putCakeMalformedJsonRequest, HttpStatus.BAD_REQUEST,
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

		System.out.println("The response body from restTemplate is:");
		System.out.println(actualJsonHalResponse);
		assertEquals(expectedJsonHalResponse, actualJsonHalResponse);
	}

}
