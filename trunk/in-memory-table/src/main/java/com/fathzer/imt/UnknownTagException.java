package com.fathzer.imt;

/** Thrown to indicate that a method has been passed an unknown tag.
 * @author Jean-Marc Astesana
 */
public class UnknownTagException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public UnknownTagException(String message) {
		super(message);
	}
}
