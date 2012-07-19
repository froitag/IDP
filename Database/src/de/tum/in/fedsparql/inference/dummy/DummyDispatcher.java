package de.tum.in.fedsparql.inference.dummy;

import de.tum.in.fedsparql.inference.Script;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.Node;

public class DummyDispatcher extends Dispatcher{

	@Override
	public void execute(Node node, Script script) {
		// TODO: Implement
		script.getScript();
	}
}
