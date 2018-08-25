package com.fathzer.imt;

/** Thrown to indicate that a method has been passed an unknown tag.
 * @author Jean-Marc Astesana
 */
public class UnknownTagException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	/** Constructor.
	 * @param message The exception's message (typically, the result of the toString() method of the duplicated tag)
	 */
	public UnknownTagException(String message) {
		super(message);
	}
}
