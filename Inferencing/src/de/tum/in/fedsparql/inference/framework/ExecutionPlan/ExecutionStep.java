package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;

/**
 * general execution step
 */
public abstract class ExecutionStep {

	public ExecutionStep(Object ID) {
		_ID = ID;
	}
	protected Object _ID=null;

	public Object getID() {
		return _ID;
	}
	@Override
	public String toString() {
		return _ID+"#"+this.getClass().getSimpleName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExecutionStep) {
			ExecutionStep step = (ExecutionStep)obj;
			System.out.println(_ID+"="+_ID);
			return step._ID.equals(this._ID);
		}

		return false;
	}

	abstract void execute(Scheduler dispatcher);

}
