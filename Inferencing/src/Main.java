
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.fedsparql.inference.dummy.DummyDatabase;
import de.tum.in.fedsparql.inference.dummy.DummyDispatcher;
import de.tum.in.fedsparql.inference.dummy.DummyIO;
import de.tum.in.fedsparql.inference.dummy.DummyMonitoring;
import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionStep;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.DBPriorityScheduler;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;
import de.tum.in.fedsparql.inference.io.Node;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// set up scripts
		Script r1 = new Script(
				"r1", // script-name
				new JenaDatabase[]{new JenaDatabase("a")}, // input databases
				new JenaDatabase[]{new JenaDatabase("b")}, // output databases
				".." // script-content
				);
		Script r2 = new Script(
				"r2", // script-name
				new JenaDatabase[]{new JenaDatabase("b")}, // input databases
				new JenaDatabase[]{new JenaDatabase("c")}, // output databases
				".." // script-content
				);
		Script r3 = new Script(
				"r3", // script-name
				new JenaDatabase[]{new JenaDatabase("a")}, // input databases
				new JenaDatabase[]{new JenaDatabase("d")}, // output databases
				".." // script-content
				);
		Script r4 = new Script(
				"r4", // script-name
				new JenaDatabase[]{new JenaDatabase("c"),new JenaDatabase("d")}, // input databases
				new JenaDatabase[]{new JenaDatabase("e")}, // output databases
				".." // script-content
				);
		Script r5 = new Script(
				"r5", // script-name
				new JenaDatabase[]{new JenaDatabase("e")}, // input databases
				new JenaDatabase[]{new JenaDatabase("e")}, // output databases
				".." // script-content
				);


		// create dependency graph
		DependencyGraph dGraph = new DependencyGraph(new Script[]{
				r1,
				r2,
				r3,
				r4,
				r5
		});

		// manually remove dependencies (actually done via GUI)
		dGraph.removeDependency(new Script("r5"), new Script("r5"));

		// print information about the graph (actually done via GUI)
		dGraph.printScripts();
		System.out.println();
		dGraph.printDirectDependencies();
		System.out.println();
		dGraph.printInheritedDependencies();
		System.out.println();
		System.out.println();

		try {
			// create execution plan
			System.out.println("EXECUTION PLAN:");
			ExecutionPlan p = new ExecutionPlan(dGraph);
			for (ExecutionStep step: p.getSteps()) {
				System.out.println(step);
			}

			System.out.println();
			System.out.println();
			System.out.println();

			// create execution-plan PNG (actually done via GUI)
			String outputPNG = "graph.png";
			System.out.println("GENERATING IMAGE: " + outputPNG);
			try {
				byte[] png = p.generatePNG();
				if (png != null) {
					FileOutputStream fos = new FileOutputStream(new File(outputPNG));
					fos.write(png);
					fos.close();
				} else {
					System.out.println("ERROR");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
			System.out.println();


			// set up execution environment
			List<Node> nodes = new ArrayList<Node>();
			nodes.add(new Node("Node1", "localhost", 2221));
			nodes.add(new Node("Node2", "localhost", 2222));
			nodes.add(new Node("Node3", "localhost", 2223));

			DummyIO io = new DummyIO(nodes);
			io.register(io.getNodeByName("Node1"), new DummyDatabase("Test1", "database/test.nt"));
			io.register(io.getNodeByName("Node1"), new DummyDatabase("Test2", "database/test.nt"));
			io.register(io.getNodeByName("Node2"), new DummyDatabase("Test3", "database/test.nt"));
			io.register(io.getNodeByName("Node2"), new DummyDatabase("Test4", "database/test.nt"));
			io.register(io.getNodeByName("Node3"), new DummyDatabase("Test5", "database/test.nt"));

			io.register(io.getNodeByName("Node1"), new JenaDatabase("a"));
			io.register(io.getNodeByName("Node1"), new JenaDatabase("b"));
			io.register(io.getNodeByName("Node2"), new JenaDatabase("c"));
			io.register(io.getNodeByName("Node2"), new JenaDatabase("d"));
			io.register(io.getNodeByName("Node3"), new JenaDatabase("e"));

			// execute execution plan
			Thread.currentThread().setName("MAIN");
			Scheduler dispatcher = new DBPriorityScheduler(dGraph, io, new DummyMonitoring(), new DummyDispatcher());
			System.out.println("EXECUTING:");
			p.execute(dispatcher);
			System.out.println("-FINISHED EXECUTION-");
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Main finished");
	}

}
