package de.tum.in.fedsparql.rts.executor;

public class FSException extends Exception {

	private static final long serialVersionUID = 8181469390676240466L;

	public FSException() {
		super();
	}

	public FSException(String message, Throwable cause) {
		super(message, cause);
	}

	public FSException(String message) {
		super(message);
	}

	public FSException(Throwable cause) {
		super(cause);
	}
}
