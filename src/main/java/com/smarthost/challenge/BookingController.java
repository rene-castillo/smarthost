package com.smarthost.challenge;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Implements the REST methods and the booking logic.
 * 
 * @author Rene Castillo
 */
@RestController
public class BookingController {
	private static final Logger LOG = LoggerFactory.getLogger(BookingController.class);
	private static final long PREMIUM_LIMIT = 100;	// TODO: could go to Spring config xml

	/**
	 * Main REST API method for the room occupancy optimization.
	 * 
	 * @param premiumRoomsAvailable number of premium rooms available for booking
	 * @param economyRoomsAvailable number of economy rooms available for booking
	 * @param customers array of long values representing the prices the customers are willing to pay
	 * @return {@link BookingResult}, never {@code null}
	 */
	@RequestMapping("/booking")
	public BookingResult bookRooms(	//
			@RequestParam(value = "premium", defaultValue = "3") int premiumRoomsAvailable,	//
			@RequestParam(value = "economy", defaultValue = "6") int economyRoomsAvailable,	//
			@RequestParam List<Long> customers) {

		// algorithm in 4 steps

		List<Customer> idcustomers = customers.stream()	// initially list of all customers, booked customers are removed
				.filter(p -> p >= 0)	// ignore customers with negative amount of money
				.map(p -> new Customer(p))	// create Customer objects with internal IDs
				.sorted(Comparator.reverseOrder())	// handle highest paying customers first
				.collect(Collectors.toList());

		LOG.debug("Room Booking Request: premiumRoomsAvailable={}, economyRoomsAvailable={}, customers={}", premiumRoomsAvailable,
				economyRoomsAvailable, listToString(idcustomers));

		// 1. book premium customers
		List<Customer> premiumRooms = idcustomers.stream()	//
				.filter(c -> c.getMoney() >= PREMIUM_LIMIT)	// only premium customers
				.limit(premiumRoomsAvailable)	// don't overbook premium rooms
				.collect(Collectors.toList());
		idcustomers.removeAll(premiumRooms);
		LOG.debug("initial premium rooms: {}", listToString(premiumRooms));

		// 2. book economy customers
		List<Customer> economyRooms = idcustomers.stream()	//
				.filter(c -> c.getMoney() < PREMIUM_LIMIT)	// only economy customers
				.limit(economyRoomsAvailable)	// don't overbook economy rooms
				.collect(Collectors.toList());
		idcustomers.removeAll(economyRooms);
		LOG.debug("initial economy rooms: {}", listToString(economyRooms));

		// 3. upgrade economy and other customers to premium
		if(economyRooms.size() + idcustomers.size() > economyRoomsAvailable) {
			List<Customer> upgrades = Stream.concat(economyRooms.stream(), idcustomers.stream())	// booked economy customers and unbooked
																																// customers can be upgraded
					.sorted(Comparator.reverseOrder())	// make sure
					.limit(idcustomers.size()).limit(premiumRoomsAvailable - premiumRooms.size())	// don't overbook premium rooms
					.collect(Collectors.toList());
			economyRooms.removeAll(upgrades);
			idcustomers.removeAll(upgrades);
			premiumRooms.addAll(upgrades);
			LOG.debug("upgraded customers: {}", listToString(upgrades));

			// 4. book customers into economy rooms that are now free after the upgrade
			List<Customer> additionalEconomyCustomers = idcustomers.stream()	//
					.filter(c -> c.getMoney() < PREMIUM_LIMIT)	// only economy customers
					.limit(economyRoomsAvailable - economyRooms.size())	// don't overbook economy rooms
					.collect(Collectors.toList());
			idcustomers.removeAll(additionalEconomyCustomers);
			economyRooms.addAll(additionalEconomyCustomers);
			LOG.debug("additional economy customers: {}", listToString(additionalEconomyCustomers));

			LOG.debug("final premium rooms: {}", listToString(premiumRooms));
			LOG.debug("final economy rooms: {}", listToString(economyRooms));
		}

		LOG.debug("customers without a room: {}", listToString(idcustomers));

		return new BookingResult(premiumRooms.size(), economyRooms.size(),
				premiumRooms.stream().mapToLong(Customer::getMoney).sum() + economyRooms.stream().mapToLong(Customer::getMoney).sum());
	}

	/**
	 * build a list representation of the customers, using {@link Customer#toString()}
	 */
	private String listToString(List<Customer> list) {
		return "[" + list.stream().map(Customer::toString).collect(Collectors.joining(", ")) + "]";
	}
}