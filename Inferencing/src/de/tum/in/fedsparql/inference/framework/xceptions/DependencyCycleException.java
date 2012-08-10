package de.tum.in.fedsparql.inference.framework.xceptions;

/**
 * Exception stating that a dependency cycle within a DependencyGraph disturbed the processing.
 */
public class DependencyCycleException extends Exception {

	/*
	 * constructors
	 */
	/**
	 * @see Exception#Exception(String)
	 */
	public DependencyCycleException(String message) {
		super(message);
	}
	/**
	 * @see Exception#Exception(Throwable)
	 */
	public DependencyCycleException(Throwable cause) {
		super(cause);
	}
	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public DependencyCycleException(String message, Throwable cause) {
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
