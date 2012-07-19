package de.tum.in.fedsparql.inference.dummy;

import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Node;

public class DummyMonitoring extends Monitoring {

	@Override
	public Load monitor(Node node) {
		return new Load(Math.random(), Math.random());
	}
}
