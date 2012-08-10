package de.tum.in.fedsparql.inference.framework.ExecutionPlanSteps;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;



/**
 * Execution End point
 */
public class Finish extends SynchronizationPoint {

	/* constructors */
	/**
	 * @param ID this step's ID
	 */
	public Finish(Object ID) {
		super(ID);
	}


	/* overridden methods */
	/**
	 * terminate execution
	 * @throws Exception
	 */
	@Override
	void execute(Scheduler scheduler) throws Exception {
		super.execute(scheduler);

		if (_doneSteps.containsAll(this.waitFor)) {
			System.out.println("FINISH");
			Thread.currentThread().interrupt();
		}
	}

}
