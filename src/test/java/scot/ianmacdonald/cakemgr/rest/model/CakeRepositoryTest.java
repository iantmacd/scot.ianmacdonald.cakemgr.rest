package scot.ianmacdonald.cakemgr.rest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CakeRepositoryTest {

	private static final Cake lemonCheesecake = new Cake("Lemon Cheesecake", "Lemony, Creamy, Cheesy and DEEELLISSSHHUSSS",
			"http://www.cakes.org/pics/lemoncheessecake.jpg");
	
	private static final Cake chocolateCake = new Cake("Chocolate Cake", "You can never go wrong with chok let",
			"http://www.cakes.org/pics/chocolatecake.jpg");
	
	private static final Cake banoffeePie = new Cake("BanoffeePie", "Is it banana or toffee?",
			"http://www.cakes.org/pics/banoffeepie.jpg");
	
	private static List<Cake> expectedCakeList = new ArrayList<>();
	
	private static List<Cake> expectedCakeListAfterSave = null;
	
	private static CakeRepository cakeRepository = null;

	@BeforeAll
	public static void setUpClassTestData(@Autowired CakeRepository cakeRepository) {
		
		CakeRepositoryTest.cakeRepository = cakeRepository;
		
		expectedCakeList.add(lemonCheesecake);
		expectedCakeList.add(chocolateCake);
		
		expectedCakeListAfterSave = new ArrayList<Cake>(expectedCakeList);
		expectedCakeListAfterSave.add(banoffeePie);
	}
	
	@BeforeEach
	public void setupTestData() {
		
		cakeRepository.save(lemonCheesecake);
		cakeRepository.save(chocolateCake);
	}
	
	@AfterEach
	public void tearDownTestData() {
		
		cakeRepository.deleteAll();
	}
	
	@Test
	public void testReadAll() {

		// retrieve the data using the repository
		List<Cake> actualCakeList = cakeRepository.findAll();

		// test that the actual cakes are the same as the expected cakes
		assertEquals(expectedCakeList, actualCakeList);
	}

	@Test
	public void testSave() {

		// save the cake through the repository method
		Cake actualBanoffePie = cakeRepository.save(banoffeePie);

		// test the cake returned from the repository is the same as the one saved
		assertEquals(banoffeePie, actualBanoffePie);
		
		// test the cake list includes the banoffee pie now
		List<Cake> actualCakeListAfterSave = cakeRepository.findAll();
		assertEquals(expectedCakeListAfterSave, actualCakeListAfterSave);
	}

	@Test
	public void testExceptionThrownWhenDuplicateNameSaved() {
		
		// test that exception is thrown when we try to commit a Cake with duplicate name
		Cake nameDuplicateCake = new Cake("Lemon Cheesecake", "Made from cream cheese and lemons",
				"http://www.cheeseythings.org/pics/lemoncheessecake.jpg");
		DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class, () -> {
			cakeRepository.save(nameDuplicateCake);
		});
		
		// test that the cause of the DataIntegrityViolationException is a constraint violation
		Throwable cause = ex.getCause();
		assertEquals(ConstraintViolationException.class, cause.getClass());
	}

}