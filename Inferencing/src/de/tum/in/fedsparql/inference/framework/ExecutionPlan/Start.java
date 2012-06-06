package de.tum.in.fedsparql.inference.framework.ExecutionPlan;


/**
 * Execution Start Point.
 * Starts execution in a separate Thread.
 */
public class Start extends ExecutionStep {

	/**
	 * next step
	 */
	public ExecutionStep next;

	/**
	 * constructor
	 * @param next
	 */
	public Start(ExecutionStep next) {
		this.next = next;
	}
	public Start() {
		this.next = null;
	}


	/**
	 * start execution
	 */
	@Override
	void execute() {
		super.execute();

		// start execution in new thread
		new ExecutionThread(this.next).start();
	}
}
