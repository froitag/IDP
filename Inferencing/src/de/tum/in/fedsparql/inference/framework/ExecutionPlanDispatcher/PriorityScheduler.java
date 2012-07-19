package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	@Override
	protected void scheduleWindow(Window window) {
		List<ThreadInfo> threads = Arrays.asList(window.threads);
		Collections.sort(threads, new Comparator<ThreadInfo>() {
			@Override
			public int compare(ThreadInfo o1, ThreadInfo o2) {
				int dependencies1 = collection.getAllDependencies(o1.script).size();
				int dependencies2 = collection.getAllDependencies(o2.script).size();
				
				if (dependencies1 > dependencies2) {
					return 1;
				}
				if (dependencies1 < dependencies2) {
					return -1;
				}
				return 0;
			}
		});
		
		for (ThreadInfo threadInfo : threads) {
			List<Node> nodes = new ArrayList<Node>(io.getNodes());
			Collections.sort(nodes, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					Load load1 = monitoring.monitor(o1);
					Load load2 = monitoring.monitor(o2);
					double totalLoad1 = load1.cpu * 0.5 + load1.io * 0.5;
					double totalLoad2 = load2.cpu * 0.5 + load2.io * 0.5;
					
					if (totalLoad1 > totalLoad2) {
						return 1;
					}
					if (totalLoad1 < totalLoad2) {
						return -1;
					}
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
