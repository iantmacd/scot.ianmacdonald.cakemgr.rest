package scot.ianmacdonald.cakemgr.rest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
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

	private final Cake lemonCheesecake = new Cake("Lemon Cheesecake", "Lemony, Creamy, Cheesy and DEEELLISSSHHUSSS",
			"http://www.cakes.org/pics/lemoncheessecake.jpg");
	
	private final Cake chocolateCake = new Cake("Chocolate Cake", "You can never go wrong with chok let",
			"http://www.cakes.org/pics/chocolatecake.jpg");
	
	private final Cake banoffeePie = new Cake("BanoffeePie", "Is it banana or toffee? No, just taysteee!",
			"http://www.cakes.org/pics/banoffeepie.jpg");
	
	private final Cake nameDuplicateCake = new Cake("Lemon Cheesecake", "Made from cream cheese and lemons",
			"http://www.cheeseythings.org/pics/lemoncheessecake.jpg");
	
	private List<Cake> expectedCakeList = new ArrayList<>(Arrays.asList(lemonCheesecake, chocolateCake));
	
	private List<Cake> expectedCakeListAfterSave = new ArrayList<>(Arrays.asList(lemonCheesecake, chocolateCake, banoffeePie));
	
	@Autowired
	CakeRepository cakeRepository = null;
	
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
		Cake actualBanoffeePie = cakeRepository.save(banoffeePie);

		// test the cake returned from the repository is the same as the one saved
		assertEquals(banoffeePie, actualBanoffeePie);
		
		// test the cake list includes the banoffee pie now
		List<Cake> actualCakeListAfterSave = cakeRepository.findAll();
		assertEquals(expectedCakeListAfterSave, actualCakeListAfterSave);
	}

	@Test
	public void testExceptionThrownWhenDuplicateNameSaved() {
		
		// test that exception is thrown when we try to commit a Cake with duplicate name
		DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class, () -> {
			cakeRepository.save(nameDuplicateCake);
		});
		
		// test that the cause of the DataIntegrityViolationException is a constraint violation
		Throwable cause = ex.getCause();
		assertEquals(ConstraintViolationException.class, cause.getClass());
		assertEquals("could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 1\"; SQL statement:\n"
				+ "insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement", ex.getMessage());
		
		// test that the duplicate cake was not created
		List<Cake> actualCakeList = cakeRepository.findAll();
		assertEquals(expectedCakeList, actualCakeList);
	}

}