package de.tum.in.fedsparql.inference.framework.ExecutionPlan;



/**
 * End point
 */
public class Finish extends SynchronizationPoint {


	public Finish(Object ID) {
		super(ID);
	}

	/**
	 * terminate execution
	 */
	@Override
	void execute(Dispatcher dispatcher) {
		super.execute(dispatcher);

		if (_doneSteps.containsAll(this.waitFor)) {
			System.out.println("FINISH");
			Thread.currentThread().interrupt();
		}
	}

}
