package com.fathzer.imt.util;

public class UnexpectedCloneNotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public UnexpectedCloneNotSupportedException(CloneNotSupportedException e) {
		super(e);
	}
}
