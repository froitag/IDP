import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;


public class Main2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Script r1 = new Script(
				new String[]{"a"},
				new String[]{"b"},
				".."
				);
		Script r2 = new Script(
				new String[]{"b"},
				new String[]{"c"},
				".."
				);
		Script r3 = new Script(
				new String[]{"a"},
				new String[]{"d"},
				".."
				);
		Script r4 = new Script(
				new String[]{"c","d"},
				new String[]{"e"},
				".."
				);
		Script r5 = new Script(
				new String[]{"f"},
				new String[]{"e"},
				".."
				);

		ScriptCollection scripts = new ScriptCollection(new Script[]{
				r1,
				r2,
				r3,
				r4,
				r5
		});


	}

}
