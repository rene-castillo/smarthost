package com.smarthost.challenge;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents customers with not only the amount of money a customer is ready to pay but also an ID to be able to distinguish two customers
 * having the same amount of money. Customers are immutable.
 * 
 * @author Rene Castillo Created 02.09.2018
 */
public class Customer implements Comparable<Customer> {
	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	private final long id;
	private final long money;

	/**
	 * Creates a new customer, the ID is generated internally.
	 * 
	 * @param money
	 */
	public Customer(long money) {
		id = ID_GENERATOR.getAndIncrement();
		this.money = money;
	}

	public long getId() {
		return id;
	}

	public long getMoney() {
		return money;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (money ^ (money >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if(id != other.id)
			return false;
		if(money != other.money)
			return false;
		return true;
	}

	@Override
	public int compareTo(Customer other) {
		if(money < other.money)
			return -1;

		if(money > other.money)
			return 1;

		// money == other.money, now compare the IDs to get a consistent order
		return ((Long) id).compareTo(other.id);
	}

	@Override
	public String toString() {
		return money + "";
	}
}