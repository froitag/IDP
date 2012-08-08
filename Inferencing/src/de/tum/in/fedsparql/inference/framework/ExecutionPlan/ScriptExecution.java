package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;

/**
 * Script Execution
 */
public class ScriptExecution extends ExecutionStep {

	public ScriptExecution(Object ID) {
		super(ID);
	}
	public ScriptExecution(Object ID, Script script, DependencyGraph dependencyGraph) {
		this(ID);
		this.script = script;
		this.dependencyGraph = dependencyGraph;
	}


	/**
	 * Script to execute
	 */
	public Script script=null;
	/**
	 * ScriptCollection this.script belongs to
	 */
	public DependencyGraph dependencyGraph=null;
	/**
	 * Next step
	 */
	public ExecutionStep next=null;

	/**
	 * Execute Script and goto next step
	 * @throws Exception
	 */
	@Override
	void execute(Scheduler scheduler) throws Exception {
		System.out.println(this);
		if (Thread.currentThread() instanceof ExecutionThread) {
			((ExecutionThread) Thread.currentThread()).executionStep = this;
		}

		scheduler.executeInternal(this.script, this.dependencyGraph);
		this.next.execute(scheduler);
	}


	/**
	 * overridden toString()
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " " + this.script + "; -> " + (this.next!=null?this.next.getID():"NULL");
		return str;
	}
}
