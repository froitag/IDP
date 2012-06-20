package de.tum.in.fedsparql.inference.framework.ExecutionPlan;




class ExecutionThread extends Thread {

	public ExecutionStep executionStep;
	public Dispatcher dispatcher;

	public ExecutionThread(ExecutionStep step, Dispatcher dispatcher) {
		this.executionStep = step;
		this.dispatcher = dispatcher;
	}


	@Override
	public void run() {
		this.executionStep.execute(this.dispatcher);
	}


}
