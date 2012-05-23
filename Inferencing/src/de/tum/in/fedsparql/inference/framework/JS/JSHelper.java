package de.tum.in.fedsparql.inference.framework.JS;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * helps compiling / evaluating javascript.
 * 
 */
public class JSHelper {

	/* constructor */
	protected JSHelper() {

	}


	/* public static methods */
	/**
	 * compiles a given javascript into bytecode that may easily be executed multiple times using this.eval().
	 * 
	 * @param js
	 * @return compiled javascript
	 * @throws ScriptException
	 */
	public static CompiledScript compile(String js) throws ScriptException {
		return _jsEngine.compile(js);
	}

	/**
	 * executes a precompiled javascript and makes the given `arguments` available within the script.
	 * 
	 * @param script
	 * @param arguments String=>Object Map, the key is used as name for the JS variable
	 * @return Object, last statement of the executed script
	 * @throws ScriptException
	 */
	public static Object eval(CompiledScript script, Map<String,Object> arguments) throws ScriptException {
		Bindings bindings = new SimpleBindings();

		if (arguments != null) {
			for (String alias: arguments.keySet()) {
				bindings.put(alias, arguments.get(alias));
			}
		}

		return script.eval(bindings);
	}







	/* protected member */
	protected static Compilable _jsEngine=(Compilable) new ScriptEngineManager().getEngineByName("JavaScript");
}
