package de.tum.in.fedsparql.inference.framework.plan;

import de.tum.in.fedsparql.inference.framework.plandispatcher.Scheduler;



/**
 * Thread derivative for executing a ExecutionPlan-Step.
 * Tracks the current ExecutionStep, Scheduler and the Exception that possibly terminated the execution.
 */
class ExecutionThread extends Thread {

	/* public member */
	public ExecutionStep executionStep;
	public Scheduler scheduler;
	public Exception exception = null;


	/* constructors */
	/**
	 * @param step the ExecutionStep this Thread should execute
	 * @param scheduler the Scheduler to use for the execution
	 */
	public ExecutionThread(ExecutionStep step, Scheduler scheduler) {
		this.executionStep = step;
		this.scheduler = scheduler;

		this.setName("ExecutionThread: " + this.executionStep.toString());
	}


	/* overridden methods */
	/**
	 * Calls this.executionStep's execute() + saves a thrown exception in this.exception
	 */
	@Override
	public void run() {
		try {
			this.executionStep.execute(this.scheduler);
		} catch (Exception e) {
			this.exception = e;
		}
	}
}
