
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
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionStep;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.PriorityScheduler;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.SimpleScheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;
import de.tum.in.fedsparql.inference.io.Node;


public class Main {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {


		// set up scripts
		Script r1 = new Script(
				"r1",
				new JenaDatabase[]{new JenaDatabase("a")},
				new JenaDatabase[]{new JenaDatabase("b")},
				".."
				);
		Script r2 = new Script(
				"r2",
				new JenaDatabase[]{new JenaDatabase("b")},
				new JenaDatabase[]{new JenaDatabase("c")},
				".."
				);
		Script r3 = new Script(
				"r3",
				new JenaDatabase[]{new JenaDatabase("a")},
				new JenaDatabase[]{new JenaDatabase("d")},
				".."
				);
		Script r4 = new Script(
				"r4",
				new JenaDatabase[]{new JenaDatabase("c"),new JenaDatabase("d")},
				new JenaDatabase[]{new JenaDatabase("e")},
				".."
				);
		Script r5 = new Script(
				"r5",
				new JenaDatabase[]{new JenaDatabase("e")},
				new JenaDatabase[]{new JenaDatabase("e")},
				".."
				);
		//		Script r6 = new Script(
		//				"r6",
		//				new JenaDatabase[]{new JenaDatabase("c")},
		//				new JenaDatabase[]{new JenaDatabase("e")},
		//				".."
		//				);


		ScriptCollection scripts;
		try {
			// create script collection
			scripts = new ScriptCollection(new Script[]{
					r1,
					r2,
					r3,
					r4,
					r5,
					//					r6
			});
			scripts.removeDependency(new Script("r5"), new Script("r5"));

			scripts.printScripts();
			System.out.println();
			scripts.printDirectDependencies();
			System.out.println();
			scripts.printInheritedDependencies();
			System.out.println();
			//			scripts.printIndependentlyProcessableScripts();
			System.out.println();



			// create execution plan
			System.out.println("EXECUTION PLAN:");
			ExecutionPlan p = new ExecutionPlan(scripts);
			for (ExecutionStep step: p.getSteps()) {
				System.out.println(step);
			}

			System.out.println();
			System.out.println();
			System.out.println();

			// create PNG
			String outputPNG = "graph.png";
			System.out.println("GENERATING IMAGE: " + outputPNG);
			try {
				byte[] png = p.generatePNG();
				if (png != null) {
					FileOutputStream fos = new FileOutputStream(new File(outputPNG));
					fos.write(png);
				} else {
					System.out.println("ERROR");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
			System.out.println();
			
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

			// execute execution plan
			Scheduler dispatcher = new PriorityScheduler(scripts, io, new DummyMonitoring(), new DummyDispatcher());
			System.out.println("EXECUTING:");
			p.execute(dispatcher);
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
