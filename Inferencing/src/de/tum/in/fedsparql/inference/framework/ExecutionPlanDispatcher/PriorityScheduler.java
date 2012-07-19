package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Monitoring.Load;
import de.tum.in.fedsparql.inference.io.Node;

public class PriorityScheduler extends WindowedScheduler {

	public PriorityScheduler(ScriptCollection collection, IO io,
			Monitoring monitoring, Dispatcher dispatcher) {
		super(collection, io, monitoring, dispatcher);
		// TODO Auto-generated constructor stub
	}
	
	protected double getScriptPriority(Script script) {
		return collection.getAllDependencies(script).size();
	}
	
	protected double getNodePriority(Script script, Node node) {
		Load load = monitoring.monitor(node);
		return (1 - load.cpu) * 0.5 + (1 - load.io) * 0.5;
	}

	@Override
	protected void scheduleWindow(Window window) {
		List<ThreadInfo> threads = Arrays.asList(window.threads);
		Collections.sort(threads, new Comparator<ThreadInfo>() {
			@Override
			public int compare(ThreadInfo o1, ThreadInfo o2) {
				double priority1 = getScriptPriority(o1.script);
				double priority2 = getScriptPriority(o2.script);
				
				if (priority1 > priority2)
					return 1;
				if (priority1 < priority2)
					return -1;
				return 0;
			}
		});
		
		for (final ThreadInfo threadInfo : threads) {
			List<Node> nodes = new ArrayList<Node>(io.getNodes());
			Collections.sort(nodes, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					double priority1 = getNodePriority(threadInfo.script, o1);
					double priority2 = getNodePriority(threadInfo.script, o2);
					
					if (priority1 > priority2)
						return 1;
					if (priority1 < priority2)
						return -1;
					return 0;
				}
			});
			
			if (nodes.isEmpty()) {
				//start execution without specifying a node
				threadInfo.startExecution(null);
			} else {
				//start execution using the first (best) node
				threadInfo.startExecution(nodes.get(0));
			}
		}
	}

}
