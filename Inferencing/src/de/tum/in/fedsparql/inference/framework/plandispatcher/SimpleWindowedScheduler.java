package de.tum.in.fedsparql.inference.framework.plandispatcher;

import java.util.List;

import de.tum.in.fedsparql.inference.framework.graph.DependencyGraph;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Node;

public class SimpleWindowedScheduler extends WindowedScheduler {

	public SimpleWindowedScheduler(DependencyGraph collection, IO io,
			Monitoring monitoring, Dispatcher dispatcher) {
		super(collection, io, monitoring, dispatcher);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void scheduleWindow(Window window) {
		List<Node> nodes = io.getNodes();
		int i = 0;
		for (ThreadInfo threadInfo : window.threads) {
			threadInfo.startExecution(nodes.get(i));
			i++;
			if (i >= nodes.size()) {
				i = 0;
			}
		}
	}

}
