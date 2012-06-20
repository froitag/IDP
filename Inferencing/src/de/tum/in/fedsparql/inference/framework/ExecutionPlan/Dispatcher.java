package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;

/**
 * Dispatcher.
 * Executes Scripts on real environment.
 *
 */
public abstract class Dispatcher {

	public abstract void dispatch(Script script, ScriptCollection collection);

}
