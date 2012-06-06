package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashSet;
import java.util.Set;


/**
 * Fork, splits execution up into multiple parallel executions.
 * Spawns threads for every parallel path.
 */
public class Fork extends ExecutionStep {

	/**
	 * Next parallel executable steps
	 */
	public Set<ExecutionStep> branches;

	/**
	 * constructor
	 * @param branches
	 */
	public Fork(Set<ExecutionStep> branches) {
		this.branches = branches;
	}
	public Fork() {
		this.branches = new HashSet<ExecutionStep>();
	}


	/**
	 * fork execution
	 * @throws InterruptedException
	 */
	@Override
	void execute() {
		super.execute();

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
