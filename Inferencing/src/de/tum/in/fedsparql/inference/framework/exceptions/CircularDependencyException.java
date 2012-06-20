package de.tum.in.fedsparql.inference.framework.exceptions;

/**
 * Exception stating that a Circular Dependency within a ScriptCollection disturbed the processing.
 */
public class CircularDependencyException extends Exception {

	/*
	 * constructors
	 */
	/**
	 * @see Exception#Exception(String)
	 */
	public CircularDependencyException(String message) {
		super(message);
	}
	/**
	 * @see Exception#Exception(Throwable)
	 */
	public CircularDependencyException(Throwable cause) {
		super(cause);
	}
	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public CircularDependencyException(String message, Throwable cause) {
		super(message,cause);
	}


	/*
	 * private member
	 */
	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 5451795954418258150L;
}
