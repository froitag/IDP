package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;



/**
 * Execution Start Point.
 * Starts execution in a separate Thread.
 */
public class Start extends ExecutionStep {

	public Start(Object ID) {
		super(ID);
	}


	/**
	 * next step
	 */
	public ExecutionStep next = null;

	/**
	 * start execution
	 */
	@Override
	void execute(Scheduler scheduler) throws Exception {
		System.out.println("START");

		// start execution in new thread
		ExecutionThread thread = new ExecutionThread(this.next, scheduler);
		thread.start();
		thread.join();

		if (thread.exception != null) {
			throw thread.exception;
		}
	}


	/**
	 * overridden toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " -> " + this.next.getID();
	}
}
