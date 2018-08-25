package com.fathzer.imt.util;

/** Thrown to indicate that a clone operation was invoked on a non clonable object.
 * <br>The advantage vs {@link CloneNotSupportedException} is that UnexpectedCloneNotSupportedException is a RuntimeException.
 */
public class UnexpectedCloneNotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	/** Constructor.
	 * @param e The original exception
	 */
	public UnexpectedCloneNotSupportedException(CloneNotSupportedException e) {
		super(e);
	}
}
