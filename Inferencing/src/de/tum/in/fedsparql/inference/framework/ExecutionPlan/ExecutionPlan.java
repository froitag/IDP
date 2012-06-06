package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;

public class ExecutionPlan {

	/**
	 * constructor
	 * 
	 * @param sc
	 */
	public ExecutionPlan(ScriptCollection sc) {
		Set<Script> nextScripts = sc.getIndependentScripts();

		_startStep = new Start();
		_steps.add(_startStep);

		if (nextScripts.size() > 0) {
			if (nextScripts.size() > 1) {

				Set<ExecutionStep> set = new HashSet<ExecutionStep>();
				for (Script script: nextScripts) {
					//set.add(new )
				}
			}
		}
	}

	/* public methods */
	/**
	 * run plan
	 */
	public void execute() {
		_startStep.execute();
	}


	/* protected member */
	protected ScriptCollection _sc;
	protected Set<ExecutionStep> _steps;
	protected Start _startStep;
}
