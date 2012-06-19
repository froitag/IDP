
import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.framework.ExecutionEnvironment;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.ScriptScheduler;
import de.tum.in.fedsparql.inference.framework.ScriptScheduler.Schedule;
import de.tum.in.fedsparql.inference.framework.SimpleScriptScheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;


public class Main2 {

	/**
	 * @param args
	 */
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
		//		Script r5 = new Script(
		//				"r5",
		//				new String[]{"e"},
		//				new String[]{"f"},
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
					//					r5
			});

			scripts.printScripts();
			System.out.println();
			scripts.printDirectDependencies();
			System.out.println();
			scripts.printInheritedDependencies();
			System.out.println();
			//			scripts.printIndependentlyProcessableScripts();
			System.out.println();



			// create environment
			ExecutionEnvironment env = new ExecutionEnvironment(new String[]{"Knoten1","Knoten2","Knoten3"});

			// create schedule
			ScriptScheduler scheduler = new SimpleScriptScheduler(env);
			Schedule schedule = scheduler.generateSchedule(scripts);

			System.out.println(schedule);
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		}


	}

}
