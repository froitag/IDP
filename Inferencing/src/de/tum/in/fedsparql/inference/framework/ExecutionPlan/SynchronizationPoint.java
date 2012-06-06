package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashSet;
import java.util.Set;


/**
 * Synchronization Point.
 * Merges multiple parallel execution paths.
 * Terminates Threads of dead paths.
 */
public class SynchronizationPoint extends ExecutionStep {

	/**
	 * next step
	 */
	public ExecutionStep next;
	/**
	 * wait for these steps before continuing
	 */
	public Set<ExecutionStep> waitFor;


	/**
	 * constructor
	 * 
	 * @param waitFor
	 * @param next
	 */
	public SynchronizationPoint(Set<ExecutionStep> waitFor, ExecutionStep next) {
		this.waitFor = waitFor;
		this.next = next;
	}
	public SynchronizationPoint() {
		this.waitFor = new HashSet<ExecutionStep>();
		this.next = null;
	}


	/**
	 * merge execution
	 */
	@Override
	void execute() {
		super.execute();

		if (Thread.currentThread() instanceof ExecutionThread) {
			ExecutionThread t = (ExecutionThread) Thread.currentThread();

			this.waitFor.remove(t.executionStep);

			if (this.waitFor.isEmpty()) {
				// continue execution
				this.next.execute();
			} else {
				// terminate thread, wait until all other dependencies are completed
				Thread.currentThread().interrupt();
			}
		}
	}
}
