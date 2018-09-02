package com.smarthost.challenge;

/**
 * Represents the result of the booking, immutable and gets converted to JSON.
 * 
 * @author Rene Castillo
 */
public class BookingResult {
	private final int premiumRooms;
	private final int economyRooms;
	private final long totalRevenue;

	/**
	 * @param premiumRooms number of premium rooms booked
	 * @param economyRooms number of economy rooms booked
	 * @param totalRevenue total revenue of premium and economy rooms
	 */
	public BookingResult(int premiumRooms, int economyRooms, long totalRevenue) {
		this.premiumRooms = premiumRooms;
		this.economyRooms = economyRooms;
		this.totalRevenue = totalRevenue;
	}

	public int getPremiumRooms() {
		return premiumRooms;
	}

	public int getEconomyRooms() {
		return economyRooms;
	}

	public long getTotalRevenue() {
		return totalRevenue;
	}
}