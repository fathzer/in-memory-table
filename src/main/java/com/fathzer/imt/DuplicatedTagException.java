package com.fathzer.imt;

/** Thrown to indicate that we tried to add a tag already existing.
 * @author Jean-Marc Astesana
 */
public class DuplicatedTagException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	/** Constructor.
	 * @param message The exception's message (typically, the result of the toString() method of the duplicated tag)
	 */
	public DuplicatedTagException(String message) {
		super(message);
	}

}
