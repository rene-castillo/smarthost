package com.smarthost.challenge;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

/**
 * An exception indicating a failed check for consistency that was using test instances.
 * 
 * @author Rene Castillo
 */
public class TestInstancesException extends Exception {
	private static final long serialVersionUID = -3999695117323660272L;
	private Object[] evidence;

	/**
	 * Creates a new exception. The test instances are included in the {@link #toString()} output.
	 * 
	 * @param msg exception message
	 * @param evidence test instances
	 */
	public TestInstancesException(final String msg, final Object... evidence) {
		super(msg);
		this.evidence = Validate.notNull(evidence);
	}

	@Override
	public String toString() {
		String original = super.toString();
		String str = original;
		if(evidence.length > 0) {
			str += "\n";
			str += "List of problematic test instances: " + Arrays.stream(evidence).map(Object::toString).collect(Collectors.joining(", "));
		}
		return str;
	}
}