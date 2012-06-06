package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

/**
 * general execution step
 */
public abstract class ExecutionStep {

	void execute() {
		if (Thread.currentThread() instanceof ExecutionThread) {
			ExecutionThread t = (ExecutionThread) Thread.currentThread();
			t.executionStep = this;
		}
	}

}
