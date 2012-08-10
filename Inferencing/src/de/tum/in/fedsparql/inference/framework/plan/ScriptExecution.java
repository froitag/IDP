package de.tum.in.fedsparql.inference.framework.plan;

import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.plandispatcher.Scheduler;

/**
 * Script Execution
 */
public class ScriptExecution extends ExecutionStep {

	/* public member */
	/**
	 * Script to execute
	 */
	public Script script=null;
	/**
	 * The DependencyGraph this.script belongs to
	 */
	public DependencyGraph dependencyGraph=null;
	/**
	 * Next step
	 */
	public ExecutionStep next=null;


	/* constructors */
	/**
	 * @param ID this step's ID
	 */
	public ScriptExecution(Object ID) {
		super(ID);
	}
	/**
	 * @param ID this step's ID
	 * @param script the Script to execute
	 * @param dependencyGraph the DependencyGraph the Script belongs to
	 */
	public ScriptExecution(Object ID, Script script, DependencyGraph dependencyGraph) {
		this(ID);
		this.script = script;
		this.dependencyGraph = dependencyGraph;
	}


	/* overridden methods */
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
