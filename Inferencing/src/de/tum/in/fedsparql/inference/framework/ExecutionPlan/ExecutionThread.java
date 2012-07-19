package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;




class ExecutionThread extends Thread {

	public ExecutionStep executionStep;
	public Scheduler scheduler;
	
	public Exception exception = null;
	
	public ExecutionThread(ExecutionStep step, Scheduler scheduler) {
		this.executionStep = step;
		this.scheduler = scheduler;
	}


	@Override
	public void run() {
		try {
			this.executionStep.execute(this.scheduler);
		} catch (Exception e) {
			this.exception = e;
		}
	}
}
