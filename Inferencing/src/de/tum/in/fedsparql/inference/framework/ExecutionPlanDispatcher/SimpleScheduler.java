package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;

public class SimpleScheduler extends Scheduler {

	public SimpleScheduler(ScriptCollection collection) {
		super(collection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void enqueue(Script script, ScriptCollection collection) {
		System.out.println("DISPATCHER: executing " + script);
	}

}
