package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan.Dispatcher;

public class SimpleDispatcher extends Dispatcher {

	@Override
	public void dispatch(Script script, ScriptCollection collection) {
		System.out.println("DISPATCHER: executing " + script);
	}

}
