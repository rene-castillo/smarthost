/*
 * Created on 31.08.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.smarthost.challenge;

import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

	@RequestMapping("/booking")
	public BookingResult bookRooms(
			@RequestParam(value = "premiumRoomsAvailable", defaultValue = "3") int premiumRoomsAvailable,
			@RequestParam(value = "economyRoomsAvailable", defaultValue = "3") int economyRoomsAvailable) {
		return new BookingResult(2, 1, 0);
	}

}