import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;


public class Main2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {



		Script r1 = new Script(
				"r1",
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
				new String[]{"d"},
				".."
				);
		Script r4 = new Script(
				"r4",
				new String[]{"c","d"},
				new String[]{"e"},
				".."
				);
		Script r5 = new Script(
				"r5",
				new String[]{"f"},
				new String[]{"e"},
				".."
				);

		ScriptCollection scripts;
		try {
			scripts = new ScriptCollection(new Script[]{
					r1,
					r2,
					r3,
					r4,
					r5
			});

			scripts.printDependencies();
			System.out.println();
			System.out.println();
			scripts.printInheritedDependencies();
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		}


	}

}
