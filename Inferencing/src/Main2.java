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
				"r5",
				new String[]{"a"},
				new String[]{"b"},
				".."
				);
		Script r2 = new Script(
				"r2",
				new String[]{"b"},
				new String[]{"c"},
				".."
				);
		Script r3 = new Script(
				"r3",
				new String[]{"a"},
				new String[]{"d","b"},
				".."
				);
		Script r4 = new Script(
				"r4",
				new String[]{"c","d"},
				new String[]{"e"},
				".."
				);
		Script r5 = new Script(
				"r1",
				new String[]{"f"},
				new String[]{"e"},
				".."
				);

		ScriptCollection scripts;
		try {
			// create script collection
			scripts = new ScriptCollection(new Script[]{
					r1,
					r2,
					r3,
					r4,
					r5
			});

			scripts.printDirectDependencies();
			System.out.println();
			scripts.printInheritedDependencies();
			System.out.println();
			scripts.printIndependentlyProcessableScripts();
			System.out.println();

			// create environment
			ExecutionEnvironment env = new ExecutionEnvironment(new String[]{"a","b"});

			// create schedule
			ScriptScheduler scheduler = new SimpleScriptScheduler(env);
			Schedule schedule = scheduler.generateSchedule(scripts);

			System.out.println(schedule);
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		}


	}

}
