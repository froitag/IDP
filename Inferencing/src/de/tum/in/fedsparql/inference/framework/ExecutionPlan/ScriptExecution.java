package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.Script;

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
	 * Next step
	 */
	public ExecutionStep next=null;




	/**
	 * Execute Script and goto next step
	 */
	@Override
	void execute() {
		System.out.println(this);
		super.execute();

		//script.execute();
		this.next.execute();
	}
}
