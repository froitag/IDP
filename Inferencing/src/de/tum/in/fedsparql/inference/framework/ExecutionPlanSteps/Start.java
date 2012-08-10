package de.tum.in.fedsparql.inference.framework.ExecutionPlanSteps;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;



/**
 * Execution Start Point.
 * The calling Thread will get blocked until the Execution finished.
 */
public class Start extends ExecutionStep {

	/* public member */
	/**
	 * next step
	 */
	public ExecutionStep next = null;


	/* constructors */
	/**
	 * @param ID this step's ID
	 */
	public Start(Object ID) {
		super(ID);
	}


	/* overridden methods */
	/**
	 * start execution in separate thread + wait for the execution to finish + dispose the scheduler after the execution finished
	 */
	@Override
	public void execute(Scheduler scheduler) throws Exception {
		System.out.println("START");

		// start execution in new thread
		ExecutionThread thread = new ExecutionThread(this.next, scheduler);
		thread.start();
		thread.join();
		scheduler.dispose();

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
