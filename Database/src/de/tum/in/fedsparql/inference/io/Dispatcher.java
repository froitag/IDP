package de.tum.in.fedsparql.inference.io;

import de.tum.in.fedsparql.inference.Script;

public abstract class Dispatcher {

	public abstract void execute(Node node, Script script);
}
