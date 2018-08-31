/*
 * Created on 31.08.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.smarthost.challenge;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testBooking() throws Exception {
		// @formatter:off
		mvc.perform(
			MockMvcRequestBuilders.get("/booking")
			.param("premium", "1")
			.param("economy", "2")
			.param("customers", "1,2,3")
			.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andExpect(content().json("{\"premiumRooms\": 1, \"economyRooms\": 2, \"totalRevenue\": 6}"));
		// @formatter:on
	}

}