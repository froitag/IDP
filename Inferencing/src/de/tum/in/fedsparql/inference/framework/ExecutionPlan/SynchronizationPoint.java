package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionThread;


/**
 * Synchronization Point.
 * Merges multiple parallel execution paths.
 * Terminates Threads of dead paths.
 */
public class SynchronizationPoint extends ExecutionStep {

	public SynchronizationPoint(Object ID) {
		super(ID);
	}

	/**
	 * next step
	 */
	public ExecutionStep next=null;
	/**
	 * wait for these steps before continuing
	 */
	public Set<ExecutionStep> waitFor=new HashSet<ExecutionStep>();


	@Override
	public String toString() {
		String str = super.toString();
		for (ExecutionStep step: this.waitFor) {
			str += " " + step.getID();
		}
		str += "; ->" + (this.next!=null?this.next.getID():"NULL");

		return str;
	}

	/**
	 * merge execution
	 */
	@Override
	void execute() {
		System.out.println(this);

		if (Thread.currentThread() instanceof ExecutionThread) {
			ExecutionThread t = (ExecutionThread) Thread.currentThread();


			if (t.executionStep != null) {
				_doneSteps.add(t.executionStep);
			}

			if (_doneSteps.containsAll(this.waitFor)) {
				// continue execution
				System.out.println("SYNC continue");
				if (this.next != null) {
					this.next.execute();
				} else {
					Thread.currentThread().interrupt();
				}
			} else {
				// terminate thread, wait until all other dependencies are completed
				System.out.println("SYNC wait");
				Thread.currentThread().interrupt();
			}
		}
	}

	protected Set<ExecutionStep> _doneSteps=new HashSet<ExecutionStep>();
}