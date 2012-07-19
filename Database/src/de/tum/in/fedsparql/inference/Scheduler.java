package de.tum.in.fedsparql.inference;

import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;

public abstract class Scheduler {

	private DependencyGraph graph = null;
	private ExecutionPlan plan = null;
	private Monitoring monitoring = null;
	private Dispatcher dispatcher = null;
	private IO io = null;

	public Scheduler(DependencyGraph graph, ExecutionPlan plan, Dispatcher dispatcher, Monitoring monitoring, IO io){
		this.graph = graph;
		this.plan = plan;
		this.monitoring = monitoring;
		this.dispatcher = dispatcher;
		this.io = io;
	}

	protected DependencyGraph getGraph() {
		return graph;
	}

	protected ExecutionPlan getPlan() {
		return plan;
	}

	protected Monitoring getMonitoring() {
		return monitoring;
	}
	
	protected Dispatcher getDispatcher() {
		return dispatcher;
	}
	
	protected IO getIO() {
		return io;
	}

	public abstract void schedule(Script script);
}
