package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashSet;
import java.util.Set;


/**
 * Fork, splits execution up into multiple parallel executions.
 * Spawns threads for every parallel path.
 */
public class Fork extends ExecutionStep {

	public Fork(Object ID) {
		super(ID);
	}

	@Override
	public String toString() {
		String str=super.toString();
		for (ExecutionStep step: this.branches) {
			str += " ->" + step.getID();
		}

		return str;
	}


	/**
	 * Next parallel executable steps
	 */
	public Set<ExecutionStep> branches=new HashSet<ExecutionStep>();


	/**
	 * fork execution
	 * @throws InterruptedException
	 */
	@Override
	void execute() {
		System.out.println(this);

		/*
		 *  continue first branch in current thread,
		 *  spawn new threads for the rest
		 */
		Set<ExecutionStep> tempBranches = new HashSet<ExecutionStep>(this.branches);
		ExecutionStep firstBranch = tempBranches.iterator().next();
		tempBranches.remove(firstBranch);

		// spawn new threads
		for (ExecutionStep step: tempBranches) {
			new ExecutionThread(step).start();
		}

		// continue this thread
		firstBranch.execute();
	}
}
