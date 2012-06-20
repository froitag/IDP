package de.tum.in.fedsparql.inference.framework.ExecutionPlan;




public class ExecutionThread extends Thread {

	public ExecutionStep executionStep;

	public ExecutionThread(ExecutionStep step) {
		this.executionStep = step;
	}


	@Override
	public void run() {
		executionStep.execute();
	}


}
