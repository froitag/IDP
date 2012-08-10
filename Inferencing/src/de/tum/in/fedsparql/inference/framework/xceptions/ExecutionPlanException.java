package de.tum.in.fedsparql.inference.framework.xceptions;

public class ExecutionPlanException extends Exception {

	/*
	 * constructors
	 */
	/**
	 * @see Exception#Exception(String)
	 */
	public ExecutionPlanException(String message) {
		super(message);
	}
	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ExecutionPlanException(Throwable cause) {
		super(cause);
	}
	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ExecutionPlanException(String message, Throwable cause) {
		super(message,cause);
	}


	/*
	 * private member
	 */
	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -1413089396707730766L;
}
