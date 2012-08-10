package de.tum.in.fedsparql.inference.framework.ExecutionPlanSteps;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;

/**
 * general execution step
 */
public abstract class ExecutionStep {

	/* constructors */
	/**
	 * ExecutionStep Constructors must at least take the Step's ID !
	 * 
	 * @param ID the step's ID
	 */
	public ExecutionStep(Object ID) {
		_ID = ID;
	}


	/* public methods */
	/**
	 * @return the ID of this ExecutionStep
	 */
	public Object getID() {
		return _ID;
	}

	/**
	 * ExecutionStep's execution() method, to be overridden by the actual ExecutionStep
	 * @param scheduler
	 * @throws Exception
	 */
	abstract void execute(Scheduler scheduler) throws Exception;


	/* overridden methods */
	/**
	 * overridden toString()
	 */
	@Override
	public String toString() {
		return _ID+"#"+this.getClass().getSimpleName();
	}


	/* protected member */
	protected Object _ID=null;
}
