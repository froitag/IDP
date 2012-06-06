package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.Set;


/**
 * End point
 */
public class Finish extends SynchronizationPoint {

	/**
	 * constructor
	 * 
	 * @param waitFor
	 * @param next
	 */
	public Finish(Set<ExecutionStep> waitFor, ExecutionStep next) {
		super(waitFor, next);
	}
	public Finish() {
		super();
	}


	/**
	 * terminate execution
	 */
	@Override
	void execute() {
		super.execute();

		Thread.currentThread().interrupt();
	}

}
