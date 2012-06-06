package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.Script;

/**
 * Script Execution
 */
public class ScriptExecution extends ExecutionStep {

	/**
	 * Script to execute
	 */
	public Script script;
	/**
	 * Next step
	 */
	public ExecutionStep next;


	/**
	 * constructor
	 */
	public ScriptExecution(Script script, ExecutionStep next) {
		this.script = script;
		this.next = next;
	}
	public ScriptExecution() {
		this.script = null;
		this.next = null;
	}


	/**
	 * Execute Script and goto next step
	 */
	@Override
	void execute() {
		super.execute();

		//script.execute();
		this.next.execute();
	}
}
