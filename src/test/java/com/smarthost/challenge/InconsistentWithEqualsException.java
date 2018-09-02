package com.smarthost.challenge;

/**
 * Exception that indicates a violation of the contract of {@link Object#equals(Object)}.
 * 
 * @author Rene Castillo
 */
public final class InconsistentWithEqualsException extends TestInstancesException {
	private static final long serialVersionUID = -5644210319670416128L;

	/**
	 * Exception that indicates a violation of the contract of {@link Object#equals(Object)}.
	 * 
	 * @param msg exception message
	 * @param evidence test instances that were used
	 */
	public InconsistentWithEqualsException(final String msg, final Object... evidence) {
		super(msg, evidence);
	}
}