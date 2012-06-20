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
	public ExecutionStep next=null;

	@Override
	public String toString() {
		return super.toString() + " ->" + this.next.getID();
	}


	/**
	 * start execution
	 */
	@Override
	void execute(Scheduler dispatcher) {
		System.out.println("START");

		// start execution in new thread
		new ExecutionThread(this.next, dispatcher).start();
	}
}
