package com.smarthost.challenge;

import org.apache.commons.lang3.Validate;

/**
 * Provides methods to check the consistency of the implementation of {@code compareTo(T)} and {@code equals(T)}.
 * 
 * @author Rene Castillo
 */
public final class CompareToConsistencyChecker {

	/**
	 * Checks if the implementation of {@code compareTo(T)} and {@code equals(T)} in class T is consistent with equals as defined in the Java
	 * Doc. Performs O(n^2) calls to {@code compareTo(T)} and {@code equals(T)}.
	 * 
	 * @param <T>
	 *           Class to be checked
	 * @param comparables
	 *           some instances that should be used for the check
	 * @throws InconsistentWithEqualsException
	 *            if the implementation is not consistent with equals
	 */
	public static <T extends Comparable<T>> void checkCompareToAndEquals(T... comparables) throws InconsistentWithEqualsException {
		Validate.isTrue(comparables.length > 0, "At least one comparable must be specified!");

		// a.equals(b) => a.compareTo(b) == 0
		for(int i = 0; i < comparables.length; i++) {
			for(int j = 0; j < comparables.length; j++) {
				if(comparables[i].equals(comparables[j])) {
					int c = comparables[i].compareTo(comparables[j]);
					if(c != 0) {
						throw new InconsistentWithEqualsException(
								"Comparables at indices " + i + " and " + j + " are equal but compareTo(i, j) is not 0: " + c, comparables[i],
								comparables[j]);
					}
				}
			}
		}

		// a.compareTo(b) == 0 => a.equals(b)
		for(int i = 0; i < comparables.length; i++) {
			for(int j = 0; j < comparables.length; j++) {
				if(comparables[i].compareTo(comparables[j]) == 0) {
					boolean e = comparables[i].equals(comparables[j]);
					if(!e) {
						throw new InconsistentWithEqualsException(
								"Comparables at indices " + i + " and " + j + " compare to 0 but they are not equal!", comparables[i],
								comparables[j]);
					}
				}
			}
		}
	}
}
