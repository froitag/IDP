package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;




class ExecutionThread extends Thread {

	public ExecutionStep executionStep;
	public Scheduler dispatcher;

	public ExecutionThread(ExecutionStep step, Scheduler dispatcher) {
		this.executionStep = step;
		this.dispatcher = dispatcher;
	}


	@Override
	public void run() {
		this.executionStep.execute(this.dispatcher);
	}


}
