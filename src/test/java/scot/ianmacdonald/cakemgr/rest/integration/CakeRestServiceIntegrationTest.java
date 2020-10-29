package scot.ianmacdonald.cakemgr.rest.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Class to make functional integration test of running RESTful service.
 * FixMethodOrder(MethodSorters.NAME_ASCENDING) annotation is used to mandate
 * the order the tests run in so the DB does not have to be torn down and set up
 * in between tests. This is not best practice as ideally each test runs in an
 * idempotent fashion. However, the tests will be launched from maven which will
 * handle starting and stopping the application so the results are predictable.
 * 
 * @author ian.macdonald@ianmacdonald.scot
 */
@TestMethodOrder(OrderAnnotation.class)
public class CakeRestServiceIntegrationTest {

	// test data

	// Apache HTTP Client objects
	private final CloseableHttpClient httpclient = HttpClients.createDefault();
	private final HttpRequestBase getCakes = new HttpGet("http://localhost:8080/cakes");
	private final HttpRequestBase postCake = new HttpPost("http://localhost:8080/cakes");

	// http response codes

	private final String OK = "HTTP/1.1 200 ";

	private final String CREATED = "HTTP/1.1 201 ";

	private final String BAD_REQUEST = "HTTP/1.1 400 ";

	private final String FORBIDDEN = "HTTP/1.1 403 ";

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

	private final String getCakesExpectedJsonResponse = "[ {\n" + "  \"id\" : 1,\n"
			+ "  \"title\" : \"Lemon cheesecake\",\n" + "  \"desc\" : \"A cheesecake made of lemon\",\n"
			+ "  \"image\" : \"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 2,\n" + "  \"title\" : \"victoria sponge\",\n"
			+ "  \"desc\" : \"sponge with jam\",\n"
			+ "  \"image\" : \"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 3,\n" + "  \"title\" : \"Carrot cake\",\n"
			+ "  \"desc\" : \"Bugs bunnys favourite\",\n"
			+ "  \"image\" : \"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg\"\n" + "}, {\n"
			+ "  \"id\" : 4,\n" + "  \"title\" : \"Banana cake\",\n" + "  \"desc\" : \"Donkey kongs favourite\",\n"
			+ "  \"image\" : \"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 5,\n" + "  \"title\" : \"Birthday cake\",\n" + "  \"desc\" : \"a yearly treat\",\n"
			+ "  \"image\" : \"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg\"\n" + "} ]";

	private final String getCakesAgainExpectedJsonResponse = "[ {\n" + "  \"id\" : 1,\n"
			+ "  \"title\" : \"Lemon cheesecake\",\n" + "  \"desc\" : \"A cheesecake made of lemon\",\n"
			+ "  \"image\" : \"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 2,\n" + "  \"title\" : \"victoria sponge\",\n"
			+ "  \"desc\" : \"sponge with jam\",\n"
			+ "  \"image\" : \"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 3,\n" + "  \"title\" : \"Carrot cake\",\n"
			+ "  \"desc\" : \"Bugs bunnys favourite\",\n"
			+ "  \"image\" : \"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg\"\n" + "}, {\n"
			+ "  \"id\" : 4,\n" + "  \"title\" : \"Banana cake\",\n" + "  \"desc\" : \"Donkey kongs favourite\",\n"
			+ "  \"image\" : \"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg\"\n"
			+ "}, {\n" + "  \"id\" : 5,\n" + "  \"title\" : \"Birthday cake\",\n" + "  \"desc\" : \"a yearly treat\",\n"
			+ "  \"image\" : \"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg\"\n" + "}, {\n"
			+ "  \"id\" : 21,\n" + "  \"title\" : \"Rees Krispy Kreme Donut\",\n"
			+ "  \"desc\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "  \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\"\n"
			+ "} ]";

	private final String putCakeExpectedJsonResponse = "{\n" + "  \"id\" : 21,\n"
			+ "  \"title\" : \"Rees Krispy Kreme Donut\",\n" + "  \"desc\" : \"Peanut Butter Deelishhhusssnessss\",\n"
			+ "  \"image\" : \"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\"\n"
			+ "}";

	private final String putCakeDuplicateExpectedJsonResponse = "{\n"
			+ "  \"message\" : \"A cake with the title [Rees Krispy Kreme Donut] already exists in the DB\",\n"
			+ "  \"type\" : \"scot.ianmacdonald.cakemgr.webapp.model.CakeDaoConstraintViolationException\",\n"
			+ "  \"causeType\" : \"org.hibernate.exception.ConstraintViolationException\",\n"
			+ "  \"causeMessage\" : \"could not execute statement\"\n" + "}";

	private final String putCakeMalformedJsonExpectedJsonResponse = "{\n"
			+ "  \"message\" : \"Problem converting JSON object to scot.ianmacdonald.cakemgr.webapp.model.CakeEntity\",\n"
			+ "  \"type\" : \"scot.ianmacdonald.cakemgr.webapp.model.PojoJsonConverterException\",\n"
			+ "  \"causeType\" : \"com.fasterxml.jackson.core.JsonParseException\",\n"
			+ "  \"causeMessage\" : \"Unexpected character (':' (code 58)): was expecting double-quote to start field name\\n at [Source: {\\n  :title\\\" : \\\"Rees Krispy Kreme Donut\\\",\\n  \\\"desc\\\" : \\\"Peanut Butter Deelishhhusssnessss\\\",\\n  \\\"image\\\" : \\\"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg\\\"\\n}; line: 2, column: 4]\"\n"
			+ "}";

	@Test
	@Order(1)
	public void testGetCakes() throws Exception {

		testRequest(getCakes, null, OK, "Lemon cheesecake");

	}

	@Test
	@Order(2)
	public void testPostCake() throws Exception {

		testRequest(postCake, putCakeJsonRequest, CREATED, "Rees Krispy Kreme Donut");

	}

	@Test
	@Order(3)
	public void testPostDuplicateCake() throws Exception {

		testRequest(postCake, putCakeJsonRequest, FORBIDDEN, "It is forbidden to create a Cake with a duplicate title");

	}

	@Test
	@Order(4)
	public void testGetCakesAgain() throws Exception {

		testRequest(getCakes, null, OK, "Rees Krispy Kreme Donut");

	}

	@Test
	@Order(5)
	public void testPostCakeMalformedJson() throws Exception {

		testRequest(postCake, putCakeMalformedJsonRequest, BAD_REQUEST, "The JSON message in the HTTP request was malformed");

	}

	private void testRequest(final HttpRequestBase request, final String jsonRequestBody,
			final String expectedReponseCode, final String expectedJsonResponse)
			throws IOException, ClientProtocolException {

		// Printing the method used
		System.out.println("Request Type: " + request.getMethod());

		// perform http post specific setup
		if ((request instanceof HttpPost)) {

			// cannot process an HTTP POST request without a JSON request body
			if (jsonRequestBody == null) {
				fail("Cannot test an HTTP POST call without a JSON request body");
			}

			// output the request body
			System.out.println("Request Body:");
			System.out.println(jsonRequestBody);

			// carry on with setup for a post request
			StringEntity entity = new StringEntity(jsonRequestBody);
			((HttpPost) request).setEntity(entity);
			request.setHeader("Content-Type", "application/json");

		}

		// get and post requests both expect response of application/json
		request.setHeader("Accept", "application/json");

		// Execute the request
		HttpResponse httpResponse = httpclient.execute(request);

		// get the HTTP response code
		final StatusLine httpResponseStatusLine = httpResponse.getStatusLine();
		final String actualHttpResponseCode = toStingNullSafe(httpResponseStatusLine);

		// get the HTTP response Content-Type header
		final Header httpResonseContentTypeHeader = httpResponse.getFirstHeader("Content-Type");
		final String actualHttpResponseContentTypeHeader = toStingNullSafe(httpResonseContentTypeHeader);

		// get the InputStream for the response
		InputStream inputStream = httpResponse.getEntity().getContent();
		// convert HTTP response body to a String
		final String actualJsonResponseBody = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining("\n"));

		// output the response code
		System.out.println("Response Code: " + actualHttpResponseCode);

		// output the response headers
		System.out.println(actualHttpResponseContentTypeHeader);

		// output the response body
		System.out.println("Response Body:");
		System.out.println(actualJsonResponseBody + "\n");

		// assert that the response code had the expected value
		assertEquals(expectedReponseCode, actualHttpResponseCode);
		// assert that the Content-Type header is application/json
		assertEquals("Content-Type: application/json", actualHttpResponseContentTypeHeader);
		// assert that the response body had the expected value
		assertTrue(actualJsonResponseBody.contains(expectedJsonResponse));

	}

	/**
	 * Get a String representation of the object in a NullPointerException safe
	 * manner
	 */
	private String toStingNullSafe(final Object object) {
		final String actualHttpResponseCode = (object == null) ? "" : object.toString();
		return actualHttpResponseCode;
	}
}
