
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.fedsparql.inference.dummy.DummyDatabase;
import de.tum.in.fedsparql.inference.dummy.DummyDispatcher;
import de.tum.in.fedsparql.inference.dummy.DummyIO;
import de.tum.in.fedsparql.inference.dummy.DummyMonitoring;
import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.framework.DatabaseID;
import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.DBPriorityScheduler;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.DependencyCycleException;
import de.tum.in.fedsparql.inference.io.Node;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// set up scripts
		Script r1 = new Script(
				"r1", // script-name
				new DatabaseID[]{new DatabaseID("a")}, // input databases
				new DatabaseID[]{new DatabaseID("b",true)}, // output databases
				".." // script-content
				);
		Script r2 = new Script(
				"r2", // script-name
				new DatabaseID[]{new DatabaseID("b")}, // input databases
				new DatabaseID[]{new DatabaseID("a")}, // output databases
				".." // script-content
				);
		/*Script r1 = new Script(
				"r1", // script-name
				new DatabaseID[]{new DatabaseID("a")}, // input databases
				new DatabaseID[]{new DatabaseID("b")}, // output databases
				".." // script-content
				);
		Script r2 = new Script(
				"r2", // script-name
				new DatabaseID[]{new DatabaseID("b")}, // input databases
				new DatabaseID[]{new DatabaseID("c")}, // output databases
				".." // script-content
				);
		Script r3 = new Script(
				"r3", // script-name
				new DatabaseID[]{new DatabaseID("a")}, // input databases
				new DatabaseID[]{new DatabaseID("d")}, // output databases
				".." // script-content
				);
		Script r4 = new Script(
				"r4", // script-name
				new DatabaseID[]{new DatabaseID("c"),new DatabaseID("d")}, // input databases
				new DatabaseID[]{new DatabaseID("e")}, // output databases
				".." // script-content
				);
		Script r5 = new Script(
				"r5", // script-name
				new DatabaseID[]{new DatabaseID("e")}, // input databases
				new DatabaseID[]{new DatabaseID("e")}, // output databases
				".." // script-content
				);*/


		// create dependency graph
		DependencyGraph dGraph = new DependencyGraph(new Script[]{
				r1,
				r2/*,
				r3,
				r4,
				r5*/
		});
		try {
			dGraph.generatePNG().save(new File("dependencies.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// manually remove dependencies (actually done via GUI)
		dGraph.removeDependency(new Script("r5"), new Script("r5"));


		// print information about the graph (actually done via GUI)
		dGraph.printScripts();
		System.out.println();
		dGraph.printDirectDependencies();
		System.out.println();
		dGraph.printInheritedDependencies();
		System.out.println();
		dGraph.printRemovedDependencies();
		System.out.println();
		System.out.println();

		try {
			// create execution plan
			ExecutionPlan p = new ExecutionPlan(dGraph);
			try {
				dGraph.generatePNG().save(new File("dependencies-nocycles.png")); // only reached if !dGraph.containsCycle() (otherwise ExecutionPlan would've thrown an CircularDependencyException)
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				p.generatePNG().save(new File("plan.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}


			System.out.println("Dependency-Graph: dependencies.png");
			System.out.println("Dependency-Graph (cycle-free): dependencies-nocycle.png");
			System.out.println("Execution-Plan: plan.png");
			System.out.println();
			System.out.println();


			//			System.out.println("EXECUTION PLAN:");
			//			for (ExecutionStep step: p.getSteps()) {
			//				System.out.println(step);
			//			}
			//			System.out.println();
			//			System.out.println();



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
			System.out.println("-EXECUTING:-");
			p.execute(dispatcher);
			System.out.println("-FINISHED EXECUTION-");

			System.out.println("\nCongratulations, you are done!");
		} catch (DependencyCycleException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
