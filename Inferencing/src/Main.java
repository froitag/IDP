
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
import de.tum.in.fedsparql.inference.framework.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.IntelligentDependencyGraph;
import de.tum.in.fedsparql.inference.framework.IntelligentDependencyGraph.DependenciesRemovalSuggestion;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.plandispatcher.DBPriorityScheduler;
import de.tum.in.fedsparql.inference.framework.plandispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.xceptions.DependencyCycleException;
import de.tum.in.fedsparql.inference.io.Node;





public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// set up scripts

		// nettes kleines Beispiel
		Script r1 = new Script( // r1: A -> B,C*
				"r1",
				new DatabaseID[]{new DatabaseID("A")},
				new DatabaseID[]{new DatabaseID("B"),new DatabaseID("C",true)}, // try without the true (without it's: r1: A -> B,C)
				"..");
		Script r2 = new Script( // r2: C,D -> A
				"r2",
				new DatabaseID[]{new DatabaseID("D"),new DatabaseID("C")},
				new DatabaseID[]{new DatabaseID("A")},
				"..");
		Script r3 = new Script( // r3: B -> D
				"r3",
				new DatabaseID[]{new DatabaseID("B")},
				new DatabaseID[]{new DatabaseID("D")},
				"..");
		Script r4 = new Script( // r4: E -> F,G
				"r4",
				new DatabaseID[]{new DatabaseID("E")},
				new DatabaseID[]{new DatabaseID("F"),new DatabaseID("G")},
				"..");
		Script r5 = new Script( // r5: F -> H,I
				"r5",
				new DatabaseID[]{new DatabaseID("F")},
				new DatabaseID[]{new DatabaseID("H"),new DatabaseID("I")},
				"..");

		// create dependency graph
		IntelligentDependencyGraph dGraph = new IntelligentDependencyGraph(new Script[]{
				r1,
				r2,
				r3,
				r4,
				r5,
		});

		/* // besonders großer Kreis
		Script r1 = new Script( // r1: A -> B
				"r1",
				new DatabaseID[]{new DatabaseID("A")},
				new DatabaseID[]{new DatabaseID("B")}, // try without the true (without it's: r1: A -> B,C)
				"..");
		Script r2 = new Script( // r2: B -> C*
				"r2",
				new DatabaseID[]{new DatabaseID("B")},
				new DatabaseID[]{new DatabaseID("C",true)},
				"..");
		Script r3 = new Script( // r3: C -> D*
				"r3",
				new DatabaseID[]{new DatabaseID("C")},
				new DatabaseID[]{new DatabaseID("D",true)},
				"..");
		Script r4 = new Script( // r4: D,F -> A*,E*
				"r4",
				new DatabaseID[]{new DatabaseID("D"),new DatabaseID("F")},
				new DatabaseID[]{new DatabaseID("A",true),new DatabaseID("E",true)},
				"..");
		Script r5 = new Script( // r5: E -> F
				"r5",
				new DatabaseID[]{new DatabaseID("E")},
				new DatabaseID[]{new DatabaseID("F")},
				"..");

		// create dependency graph
		IntelligentDependencyGraph dGraph = new IntelligentDependencyGraph(new Script[]{
				r1,
				r2,
				r3,
				r4,
				r5,
		});
		 */

		/* // mini Beispiel vom letzten Treffen
		Script r1 = new Script( // r1: a -> b*
				"r1", // script-name
				new DatabaseID[]{new DatabaseID("a")}, // input databases
				new DatabaseID[]{new DatabaseID("b",true)}, // output databases
				".." // script-content
				);
		Script r2 = new Script( // r2: b -> a
				"r2", // script-name
				new DatabaseID[]{new DatabaseID("b")}, // input databases
				new DatabaseID[]{new DatabaseID("a")}, // output databases
				".." // script-content
				);

		// create dependency graph
		IntelligentDependencyGraph dGraph = new IntelligentDependencyGraph(new Script[]{
				r1,
				r2
		});
		 */

		/*
		Script r1 = new Script(
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
				new DatabaseID[]{new DatabaseID("b")}, // input databases
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
				);
		Script r6 = new Script(
				"r6", // script-name
				new DatabaseID[]{new DatabaseID("e"),new DatabaseID("b")}, // input databases
				new DatabaseID[]{new DatabaseID("f")}, // output databases
				".." // script-content
				);
		IntelligentDependencyGraph dGraph = new IntelligentDependencyGraph(new Script[]{
				r1,
				r2,
				r3,
				r4,
				r5,
				r6
		});
		 */


		try {
			new File("dependencies.png").delete();
			new File("dependencies-afterDepRemoval.png").delete();
			new File("dependencies-execPlanInput.png").delete();
			new File("plan.png").delete();
			dGraph.generatePNG().save(new File("dependencies.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		// let the IntelligentDependencyGraph suggest some dependency removals..
		DependenciesRemovalSuggestion depRemSuggestion = dGraph.suggestDependenciesToRemove();
		dGraph.removeDependencies(depRemSuggestion);


		// manually remove additional dependencies (actually done via GUI)
		//		dGraph.removeDependency(r5, r5);
		try {
			dGraph.generatePNG().save(new File("dependencies-afterDepRemoval.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		// print information about the graph (actually done via GUI)
		dGraph.printScripts();
		System.out.println();
		System.out.println();
		dGraph.printDirectDependencies();
		System.out.println();
		dGraph.printInheritedDependencies();
		System.out.println();
		dGraph.printRemovedDependencies();
		System.out.println();
		System.out.println();
		System.out.println("-SUGGESTIONS-");
		depRemSuggestion.print();

		try {
			// create execution plan
			ExecutionPlan p = new ExecutionPlan(dGraph);
			try {
				dGraph.generatePNG().save(new File("dependencies-execPlanInput.png")); // only reached if ExecutionPlan was successfully generated
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				p.generatePNG().save(new File("plan.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}


			System.out.println("Dependency-Graph: dependencies.png");
			System.out.println("Dependency-Graph (after automatic / manual dependency removal): dependencies-aferDepRemoval.png");
			System.out.println("Execution-Plan: plan.png");
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
