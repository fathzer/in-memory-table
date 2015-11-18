package com.fathzer.imt;

public class UnknownTagException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public UnknownTagException(String message) {
		super(message);
	}
}
