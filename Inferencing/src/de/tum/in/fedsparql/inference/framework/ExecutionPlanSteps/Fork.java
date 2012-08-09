package de.tum.in.fedsparql.inference.framework.ExecutionPlanSteps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;


/**
 * Fork, splits execution up into multiple parallel executions.
 * Spawns threads for every parallel path.
 */
public class Fork extends ExecutionStep {

	public Fork(Object ID) {
		super(ID);
	}


	/**
	 * Next parallel executable steps
	 */
	public Set<ExecutionStep> branches=new HashSet<ExecutionStep>();

	/**
	 * fork execution
	 * @throws Exception
	 */
	@Override
	void execute(Scheduler scheduler) throws Exception {
		System.out.println(this);

		/*
		 *  continue first branch in current thread,
		 *  spawn new threads for the rest
		 */

		List<ExecutionThread> threads = new ArrayList<ExecutionThread>();

		//spawn the new threads
		for (ExecutionStep step : branches) {
			ExecutionThread thread = new ExecutionThread(step, scheduler);
			thread.start();
			threads.add(thread);
		}

		//wait for all threads, throw exceptions that have occurred
		for (ExecutionThread thread : threads) {
			thread.join();
			if (thread.exception != null) {
				throw thread.exception;
			}
		}
	}


	/**
	 * overridden toString()
	 */
	@Override
	public String toString() {
		String str=super.toString();
		for (ExecutionStep step: this.branches) {
			str += " ->" + step.getID();
		}

		return str;
	}
}
