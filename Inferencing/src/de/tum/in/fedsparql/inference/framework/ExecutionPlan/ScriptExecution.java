package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;

/**
 * Script Execution
 */
public class ScriptExecution extends ExecutionStep {

	public ScriptExecution(Object ID) {
		super(ID);
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += " " + this.script + "; -> " + (this.next!=null?this.next.getID():"NULL");
		return str;
	}



	/**
	 * Script to execute
	 */
	public Script script=null;
	/**
	 * ScriptCollection this.script belongs to
	 */
	public ScriptCollection scriptCollection=null;
	/**
	 * Next step
	 */
	public ExecutionStep next=null;




	/**
	 * Execute Script and goto next step
	 */
	@Override
	void execute(Scheduler dispatcher) {
		System.out.println(this);
		if (Thread.currentThread() instanceof ExecutionThread) {
			ExecutionThread t = (ExecutionThread) Thread.currentThread();

			t.executionStep = this;
		}

		dispatcher.enqueue(this.script, this.scriptCollection);
		this.next.execute(dispatcher);
	}
}
