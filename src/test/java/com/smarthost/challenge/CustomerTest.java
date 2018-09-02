package com.smarthost.challenge;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * @author Rene Castillo
 */
public class CustomerTest {

	@Test
	public void testCompareTo() throws InconsistentWithEqualsException {
		List<Customer> idcustomers = Arrays.stream(new Long[] {0L, 0L, 97L, 98L, 98L, 99L, 100L, 100L, 100L, 101L, 110L, 150L, 150L}).map(p -> new Customer(p)).collect(Collectors.toList());
		CompareToConsistencyChecker.checkCompareToAndEquals(idcustomers.toArray(new Customer[] {}));
	}
}