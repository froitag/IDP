import java.io.FileNotFoundException;
import java.util.Arrays;

import old.GUIDTranslation;
import old.InferenceIO;
import old.NormalisationScript;

import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.dummy.JenaIO;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws FSException
	 */
	public static void main(String[] args) throws FileNotFoundException, FSException {

		try {
			// init DB
			InferenceIO io = new InferenceIO(new JenaIO());
			io.addDatabase(new JenaDatabase("Test1", "database/test2.nt"));
			io.addDatabase(new JenaDatabase("Test2", "database/test3.nt"));


			// add value normalisations
			NormalisationScript s = new NormalisationScript("test","Test2", "<http://example.org#vorname>", "var x = $val.substring(1,$val.length-1).split(' '); return '\"'+x[0]+'\"';");
			io.addNormalisationScript(s);

			// add GUID translations
			GUIDTranslation t = new GUIDTranslation("Test2");
			t.addTranslation("<http://example.org#vorname>", "<http://example.org#firstname>");
			io.addTranslation(t);

			// preprocess
			io.preprocess();



			// do queries
			System.out.println("Triples in temporary database:");
			FSResultSet rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}");
			System.out.println(Arrays.toString(rs.getHeader()));
			while (rs.hasNext()){
				String[] tuple = rs.next();
				System.out.println(	tuple[rs.column("s")]+" | "+
						tuple[rs.column("p")]+" | "+
						tuple[rs.column("o")]);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("DIED!");
			e.printStackTrace();
		}

		//		JenaIO io = new JenaIO();
		//		io.register(new JenaDatabase("Test1", "database/test.nt"));
		//		io.register(new JenaDatabase("Test2", "database/test.nt"));
		//		io.register(new JenaDatabase("Test3", "database/test.nt"));
		//		io.register(new JenaDatabase("Test4", "database/test.nt"));
		//		io.register(new JenaDatabase("Test5", "database/test.nt"));
		//
		//
		//
		//		// register scripts
		//		// TODO: register scripts
		//
		//		// execute scripts
		//		// TODO: execute scripts
		//
		//		try {
		//			JSHelper jsHelper = new JSHelper();
		//
		//			Map<String,Object> scriptArgs = new HashMap<String,Object>();
		//			scriptArgs.put("obj", new JSObject());
		//
		//			CompiledScript script = jsHelper.compile("print(obj.getTitle()); a = {a: 1, b:2, toString:function(){return this.a+this.b;}};");
		//			Object result = jsHelper.eval(script, scriptArgs);
		//
		//			System.out.println(result);
		//		} catch (ScriptException e) {
		//			e.printStackTrace();
		//		}
	}

}
