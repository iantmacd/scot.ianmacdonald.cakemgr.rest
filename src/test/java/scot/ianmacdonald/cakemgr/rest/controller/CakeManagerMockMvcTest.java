package scot.ianmacdonald.cakemgr.rest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import scot.ianmacdonald.cakemgr.rest.model.Cake;
import scot.ianmacdonald.cakemgr.rest.model.CakeRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class CakeManagerMockMvcTest {

	private static Cake postRequestCake = null;

	private static List<Cake> expectedDbCakes = null;

	private static List<Cake> expectedDbCakesAfterPost = null;

	private static String postRequestCakeJson = null;

	private static String postRequestCakeBadJson = null;

	@Autowired
	private CakeRepository cakeRepository;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void initDataFromContext(@Autowired List<Cake> initialCakeList) throws JsonProcessingException {

		postRequestCake = new Cake("Rees Krispy Kreme Donut", "Peanut Butter Deelishhhusssnessss",
				"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg");

		expectedDbCakes = initialCakeList;
		expectedDbCakesAfterPost = new ArrayList<>(expectedDbCakes);
		expectedDbCakesAfterPost.add(postRequestCake);

		postRequestCakeJson = new ObjectMapper().writeValueAsString(postRequestCake);
		postRequestCakeBadJson = postRequestCakeJson.replace(':', ';');

	}

	@Test
	public void testGetCakes() throws Exception {

		ResultActions getResultActions = mockMvc.perform(get("/cakes").accept(MediaTypes.HAL_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
				.andExpect(jsonPath("$._embedded.cakes").isArray())
				.andExpect(jsonPath("$._embedded.cakes.length()", is(5)));

		testJsonValuesInCakeList(getResultActions, expectedDbCakes);
	}

	@Test
	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	public void testPostCakes() throws Exception {

		mockMvc.perform(post("/cakes").contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON)
				.content(postRequestCakeJson)).andDo(print()).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaTypes.HAL_JSON))
				.andExpect(jsonPath("$.title", is(postRequestCake.getTitle())))
				.andExpect(jsonPath("$.description", is(postRequestCake.getDescription())))
				.andExpect(jsonPath("$.image", is(postRequestCake.getImage())))
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/cakes/6")))
				.andExpect(jsonPath("$._links.cake.href", is("http://localhost/cakes/6")));
	}

	@Test
	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	public void testPostDuplicateCake() throws Exception {

		// add the Cake to the DB which will cause a duplicate to be present
		cakeRepository.save(postRequestCake);

		mockMvc.perform(post("/cakes").contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON)
				.content(postRequestCakeJson)).andDo(print()).andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaTypes.HAL_JSON)).andExpect(jsonPath("$.status", is("FORBIDDEN")))
				.andExpect(jsonPath("$.message", is("It is forbidden to create a Cake with a duplicate title")))
				.andExpect(jsonPath("$.debugMessage", is(
						"could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 6\"; SQL statement:\ninsert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")));
	}

	@Test
	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	public void testGetCakesAfterPost() throws Exception {

		// add the Cake to the DB which will cause an additional cake to be present
		cakeRepository.save(postRequestCake);

		ResultActions getResultActions = mockMvc.perform(get("/cakes").accept(MediaTypes.HAL_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
				.andExpect(jsonPath("$._embedded.cakes").isArray())
				.andExpect(jsonPath("$._embedded.cakes.length()", is(6)));

		testJsonValuesInCakeList(getResultActions, expectedDbCakesAfterPost);
	}

	@Test
	public void testPostCakeBadJson() throws Exception {

		mockMvc.perform(post("/cakes").contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON)
				.content(postRequestCakeBadJson)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaTypes.HAL_JSON))
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is("The JSON message in the HTTP request was malformed")))
				.andExpect(jsonPath("$.debugMessage", is(
						"JSON parse error: Unexpected character (';' (code 59)): was expecting a colon to separate field name and value; nested exception is com.fasterxml.jackson.core.JsonParseException: Unexpected character (';' (code 59)): was expecting a colon to separate field name and value\n at [Source: (org.springframework.mock.web.DelegatingServletInputStream); line: 1, column: 10]")));
	}

	private void testJsonValuesInCakeList(ResultActions resultActions, final List<Cake> cakeList) throws Exception {

		int i = 0;
		for (Cake cake : cakeList) {
			resultActions = resultActions.andExpect(jsonPath(getEmbeddedCakesJsonPath(i, "title"), is(cake.getTitle())))
					.andExpect(jsonPath(getEmbeddedCakesJsonPath(i, "description"), is(cake.getDescription())))
					.andExpect(jsonPath(getEmbeddedCakesJsonPath(i, "image"), is(cake.getImage())))
					.andExpect(jsonPath("$._embedded.cakes[" + i + "]._links.self.href",
							is("http://localhost/cakes/" + (i + 1))))
					.andExpect(jsonPath("$._embedded.cakes[" + i + "]._links.cake.href",
							is("http://localhost/cakes/" + (i + 1))));
			i++;
		}
		resultActions = resultActions.andExpect(jsonPath("$._links.self.href", is("http://localhost/cakes")))
				.andExpect(jsonPath("$._links.profile.href", is("http://localhost/profile/cakes")))
				.andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(i)))
				.andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.number", is(0)));
	}

	private String getEmbeddedCakesJsonPath(final int index, final String field) {
		return String.format("$._embedded.cakes[%d].%s", index, field);
	}

}