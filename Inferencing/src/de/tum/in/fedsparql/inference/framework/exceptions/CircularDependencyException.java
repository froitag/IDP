package de.tum.in.fedsparql.inference.framework.exceptions;

public class CircularDependencyException extends Exception {

	public CircularDependencyException(String message) {
		super(message);
	}
	public CircularDependencyException(Throwable cause) {
		super(cause);
	}
	public CircularDependencyException(String message, Throwable cause) {
		super(message,cause);
	}

	private static final long serialVersionUID = 1L;
}
