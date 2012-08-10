package de.tum.in.fedsparql.inference.framework.plandispatcher;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Node;

/**
 * Dispatcher.
 * Executes Scripts on real environment.
 *
 */
/**
 * @author Dominik Weber
 *
 */
public abstract class Scheduler {

	public class ThreadInfo {
		private Semaphore semaphore;
		public Script script;

		private Node node = null;

		public ThreadInfo(Script script) {
			this.semaphore = new Semaphore(0);
			this.script = script;
		}

		public void startExecution(Node node) {
			this.node = node;
			this.semaphore.release();
		}
	}

	protected DependencyGraph collection;
	protected IO io;
	protected Monitoring monitoring;
	protected Dispatcher dispatcher;

	public Scheduler(DependencyGraph collection, IO io, Monitoring monitoring, Dispatcher dispatcher) {
		this.collection = collection;
		this.io = io;
		this.monitoring = monitoring;
		this.dispatcher = dispatcher;
	}

	public abstract void schedule(ThreadInfo threadInfo);


	/**
	 * @return A random node or null of no node is available
	 */
	protected Node getRandomNode() {
		List<Node> nodes = io.getNodes();
		if (nodes.isEmpty()) {
			return null;
		}
		return nodes.get(new Random().nextInt(nodes.size()));
	}

	public void executeInternal(Script script, DependencyGraph collection) throws Exception {
		ThreadInfo threadInfo = new ThreadInfo(script);

		schedule(threadInfo);

		threadInfo.semaphore.acquire();

		if (threadInfo.node == null) {
			throw new NoNodeException("No node available");
		}

		//execute the script on the selected node
		System.out.println("SCHEDULER: executing " + threadInfo.script + " on node " + threadInfo.node);
		dispatcher.execute(threadInfo.node, threadInfo.script);
		System.out.println("SCHEDULER: execution of " + threadInfo.script + " completed");
	}

	/**
	 * gets called when the execution-plan terminated.
	 * gives the opportunity to release occupied resources.
	 */
	public void dispose() {
	}
}
