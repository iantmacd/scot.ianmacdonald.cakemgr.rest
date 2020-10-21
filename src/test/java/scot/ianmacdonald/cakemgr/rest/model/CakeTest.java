package scot.ianmacdonald.cakemgr.rest.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class CakeTest {
	
	private final Cake lemonCheeseCake_1 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_2 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_3 = new Cake("Lemon Cheesecayk", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_4 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodnesssss", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_5 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemon_cheesecake.jpg");
	private final Cake lemonCheeseCake_6 = new Cake(null, "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_7 = new Cake(null, "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_8 = new Cake("Lemon Cheesecake", null, "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_9 = new Cake("Lemon Cheesecake", null, "http://www.cakes.org/photos/lemoncheesecake.jpg");
	private final Cake lemonCheeseCake_10 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", null);
	private final Cake lemonCheeseCake_11 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", null);

	@Test
	void testEqualsMethod() {
		
		// simple case where the cakes are equal by value
		assertThat(lemonCheeseCake_1).isEqualTo(lemonCheeseCake_2);
		
		// test difference in each of the properties causes them not to be equal by value
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_3);
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_4);
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_5);
	}
	
	@Test
	void testEqualsHandlesNullTitle() {
		
		assertThat(lemonCheeseCake_6).isEqualTo(lemonCheeseCake_7);
		assertThat(lemonCheeseCake_6).isNotEqualTo(lemonCheeseCake_1);
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_6);
		
	}
	
	@Test
	void testEqualsHandlesNullDescription() {
		
		assertThat(lemonCheeseCake_8).isEqualTo(lemonCheeseCake_9);
		assertThat(lemonCheeseCake_8).isNotEqualTo(lemonCheeseCake_1);
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_8);
		
	}
	
	@Test
	void testEqualsHandlesNullImage() {
		
		assertThat(lemonCheeseCake_10).isEqualTo(lemonCheeseCake_11);
		assertThat(lemonCheeseCake_10).isNotEqualTo(lemonCheeseCake_1);
		assertThat(lemonCheeseCake_1).isNotEqualTo(lemonCheeseCake_10);
		
	}

}
