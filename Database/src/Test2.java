import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.fedsparql.inference.DependencyGraph;
import de.tum.in.fedsparql.inference.ExecutionPlan;
import de.tum.in.fedsparql.inference.Scheduler;
import de.tum.in.fedsparql.inference.Script;
import de.tum.in.fedsparql.inference.dummy.DummyDispatcher;
import de.tum.in.fedsparql.inference.dummy.DummyMonitoring;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Monitoring.Load;
import de.tum.in.fedsparql.inference.io.Node;

public class Test2 extends Scheduler{

	public Test2(DependencyGraph graph, ExecutionPlan plan,
				 Dispatcher dispatcher, Monitoring monitoring, IO io) {
		super(graph, plan, dispatcher, monitoring, io);
	}

	@Override
	public void schedule(Script script) {
	
		// Obtain the node of the first input database
		Node node = super.getIO().getNodeForDatabase(script.getInput().get(0));
		
		// Simply read the current load
		Load load = super.getMonitoring().monitor(node);
		double cpu = load.cpu;
		double io = load.io;
		
		// Execute script
		super.getDispatcher().execute(node, script);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// Prepare
		IO io = Test.buildIO();
		Script s1 = buildScript(io);
		
		// Create Scheduler
		Scheduler scheduler = new Test2(new DependencyGraph(), 
										new ExecutionPlan(),
										new DummyDispatcher(),
										new DummyMonitoring(),
										io);
		scheduler.schedule(s1);
		
	}

	private static Script buildScript(IO io) {
		
		Database in1 = io.getDatabaseByName("Test1");
		Database in2 = io.getDatabaseByName("Test3");
		Database out = io.announceDatabase(io.getNodeByName("Node3"), "Test6", Database.Type.PERSISTENT);
		
		List<Database> input = new ArrayList<Database>();
		input.add(in1);
		input.add(in2);
		
		List<Database> output = new ArrayList<Database>();
		input.add(out);
		
		return new Script(input, output, "Script1", "");
	}
}
