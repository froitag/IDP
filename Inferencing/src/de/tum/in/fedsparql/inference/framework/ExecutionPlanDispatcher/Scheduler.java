package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;

/**
 * Dispatcher.
 * Executes Scripts on real environment.
 *
 */
public abstract class Scheduler {
	
	protected ScriptCollection collection;
	
	public Scheduler(ScriptCollection collection) {
		this.collection = collection;
	}

	public abstract void enqueue(Script script, ScriptCollection collection);

}
