/*
 * Created on 31.08.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.smarthost.challenge;

public class BookingResult {
	private final int premiumRooms;
	private final int economyRooms;
	private final long totalRevenue;

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
