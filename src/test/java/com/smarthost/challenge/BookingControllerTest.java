package com.smarthost.challenge;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Assuming limit is 100 €, customers paying >= 100 € are premium customers
 * 
 * @author Rene Castillo Created 31.08.2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

	@Autowired
	MockMvc mvc;

	Resource gistFile;
	ObjectMapper mapper;
	long[] gistCustomers;

	@Before
	public void setup() throws JsonParseException, JsonMappingException, IOException {
		gistFile = new ClassPathResource("smarthost_hotel_guests.json");
		mapper = new ObjectMapper();
		gistCustomers = mapper.readValue(gistFile.getFile(), long[].class);
	}

	void testBooking(int premiumAvailable, int economyAvailable, long[] customers, int premiumBookedExpected, int economyBookedExpected,
			long totalRevenueExpected) throws Exception {
		// @formatter:off
		mvc.perform(
			MockMvcRequestBuilders.get("/booking")
			.param("premium", premiumAvailable+"")
			.param("economy", economyAvailable+"")
			.param("customers", Arrays.stream(customers).mapToObj(Long::toString).collect(Collectors.joining(",")))
			.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andExpect(content().json("{\"premiumRooms\": "+premiumBookedExpected
				+", \"economyRooms\": "+economyBookedExpected
				+", \"totalRevenue\": "+totalRevenueExpected+"}"));
		// @formatter:on
	}

	@Test
	public void testGist1() throws Exception {
		testBooking(3, 3, gistCustomers, 3, 3, 738 + 167);
	}

	@Test
	public void testGist2() throws Exception {
		testBooking(7, 5, gistCustomers, 6, 4, 1054 + 189);
	}

	@Test
	public void testGist3() throws Exception {
		testBooking(2, 7, gistCustomers, 2, 4, 583 + 189);
	}

	@Test
	public void testNoCustomers() throws Exception {
		testBooking(1, 1, new long[] {}, 0, 0, 0);	// no customers, no booking
	}

	@Test
	public void testNoRoomsAvailable() throws Exception {
		testBooking(0, 0, new long[] {99, 100}, 0, 0, 0);	// no rooms, no booking
	}

	/**
	 * premium customer must get available premium room no matter the number of available economy rooms
	 */
	@Test
	public void testPremiumCustomer() throws Exception {
		testBooking(1, 0, new long[] {100}, 1, 0, 100);
		testBooking(1, 1, new long[] {100}, 1, 0, 100);
	}

	/**
	 * economy customer must get available economy room no matter the number of available premium rooms
	 */
	@Test
	public void testEconomyCustomer() throws Exception {
		testBooking(0, 1, new long[] {99}, 0, 1, 99);
		testBooking(1, 1, new long[] {99}, 0, 1, 99);
	}

	/**
	 * premium customer must not get economy room, never
	 */
	@Test
	public void testNoDowngrade() throws Exception {
		testBooking(0, 1, new long[] {100}, 0, 0, 0); // no premium room available, no booking
		testBooking(1, 1, new long[] {100, 100}, 1, 0, 100); // premium room already taken, 2nd 100 gets no room
	}

	/**
	 * highest paying economy customer gets available premium room, if no more economy rooms are available
	 */
	@Test
	public void testUpgrade() throws Exception {
		testBooking(3, 1, new long[] {99, 98, 97, 96, 95}, 3, 1, 99+98+97+96);	// upgrade 1 economy and 2 additional customers, 95 gets no room
		testBooking(3, 0, new long[] {99, 98, 97, 96}, 3, 0, 99+98+97);	// upgrade 3 additional customers, 96 gets no room
		
		// @formatter:off
		testBooking(1, 0, new long[] {99, 98}, 1, 0, 99);	// 99 gets upgrade, 98 gets no room
		testBooking(1, 1, new long[] {99, 98, 97}, 1, 1, 99+98);	// 99 gets upgrade, 97 gets no room TODO: conceptual problem of API: can't tell who got the upgrade 99 or 98
		testBooking(1, 1, new long[] {100, 99, 98}, 1, 1, 100+99);	// single premium room is occupied by premium guest, no upgrade. 98 gets no room
		testBooking(1, 2, new long[] {99, 98}, 0, 2, 99+98);	// no upgrade because economy is not overbooked
		testBooking(1, 2, new long[] {99, 98, 97}, 1, 2, 99+98+97);	// 99 gets upgrade
		// @formatter:on
	}
	
	/**
	 * the hotel should choose the highest paying customers of each category (economy, premium)
	 */
	@Test
	public void testMaxRevenue() throws Exception {
		// @formatter:off
		testBooking(2, 2, new long[] {110, 120, 130, 70, 80, 90}, 2, 2, 130+120+90+80);	// lowest paying premium and economy guests get no room and no upgrades are possible
		// @formatter:on
	}

	/**
	 * Test more, miscellaneous cases.
	 */
	@Test
	public void testMisc() throws Exception {
		// @formatter:off
		// incremental scenario, read from top to bottom
		testBooking(4, 3, new long[] {90, 80, 70}, 0, 3, 90+80+70);	// no upgrade, economy full
		testBooking(4, 3, new long[] {90, 80, 70, 60}, 1, 3, 90+80+70+60);	// 1 upgrade TODO: conceptual problem of API: can't tell who got the upgrade
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50}, 2, 3, 90+80+70+60+50);	// 2 upgrades
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50, 110}, 3, 3, 90+80+70+60+50+110);	// premium customer arrives, still 2 upgrades
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50, 110, 120}, 4, 3, 90+80+70+60+50+110+120);	// another premium customer arrives, still 2 upgrades, hotel full
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50, 110, 120, 130}, 4, 3, 90+80+70+60+110+120+130);	// another premium customer arrives, hotel overbooked, only 1 upgrade, 1 economy guest gets no room
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50, 110, 120, 130, 140}, 4, 3, 90+80+70+110+120+130+140);	// another premium customer arrives, hotel overbooked, no upgrades, 2 economy guests get no room
		testBooking(4, 3, new long[] {90, 80, 70, 60, 50, 110, 120, 130, 140, 150}, 4, 3, 90+80+70+120+130+140+150);	// another premium customer arrives, hotel overbooked, no upgrades, 2 economy guests get no room, no downgrade, 1 premium guest gets no room
		testBooking(4, 3, new long[] {110, 120, 130, 140, 150}, 4, 0, 120+130+140+150);	// all economy guests leave, no downgrades, 1 premium guest gets no room
	// @formatter:on
	}

	/**
	 * Customers wanting to pay negative amount of money should just be ignored
	 */
	@Test
	public void testIgnoreNegativeCustomers() throws Exception {
		testBooking(1, 1, new long[] {-99, -100}, 0, 0, 0);	// no room is booked
	}

	/**
	 * Customers seeking a free night (pay 0 for the night) are allowed since the hotel policy is to satisfy the customers (maybe they will
	 * give a good rating and recommend the hotel)
	 */
	@Test
	public void testCustomersWithNoMoney() throws Exception {
		testBooking(1, 1, new long[] {0, 0}, 1, 1, 0);	// one customer even gets upgrade
	}

}